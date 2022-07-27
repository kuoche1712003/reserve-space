package io.kuoche.repository

import io.kuoche.model.bo.MinuteOfDate
import io.kuoche.model.bo.SpaceReserve
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlConnection
import io.vertx.mutiny.sqlclient.Tuple
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DatabaseSpaceReserveRepository(
    private val client: PgPool
): SpaceReserveRepository {
    override fun add(spaceReserve: SpaceReserve): Uni<Int> {
        return client.preparedQuery(
            """
                INSERT INTO public.space_date_reserve
                (unit_id, space_id, reserve_date, reserve_time)
                VALUES($1, $2, $3, B'${spaceReserve.reserveTime.toBinaryString()}');
            """.trimIndent()
        ).execute(Tuple.of(spaceReserve.unitId, spaceReserve.spaceId, spaceReserve.reserveDate))
            .map {
                it.rowCount()
            }
    }

    override fun reserveById(
        minuteOfDate: MinuteOfDate,
        unitId: String,
        spaceId: String,
        reserveDate: LocalDate,
        con: SqlConnection?
    ): Uni<Int> {
        val sql = """
            UPDATE public.space_date_reserve
            SET reserve_time = (reserve_time | B'${minuteOfDate.toBinaryString()}')
            WHERE unit_id = $1 AND space_id = $2 AND reserve_date = $3 AND 
            (reserve_time & B'${minuteOfDate.toBinaryString()}') = B'${MinuteOfDate.EMPTY.toBinaryString()}'
        """.trimIndent()
        val query = if(con == null){
            client.preparedQuery(sql)
        }else{
            con.preparedQuery(sql)
        }
        return query.execute(Tuple.of(unitId, spaceId, reserveDate))
            .map {
                it.rowCount()
            }
    }

    override fun cancelReserveById(
        minuteOfDate: MinuteOfDate,
        unitId: String,
        spaceId: String,
        reserveDate: LocalDate,
        con: SqlConnection?
    ): Uni<Int> {
        val sql = """
            UPDATE public.space_date_reserve
            SET reserve_time = (reserve_time # B'${minuteOfDate.toBinaryString()}')
            WHERE unit_id = $1 AND space_id = $2 AND reserve_date = $3 AND 
            reserve_time = (reserve_time | B'${minuteOfDate.toBinaryString()}')
        """.trimIndent()
        val query = if(con == null){
            client.preparedQuery(sql)
        }else{
            con.preparedQuery(sql)
        }
        return query.execute(Tuple.of(unitId, spaceId, reserveDate))
            .map {
                it.rowCount()
            }
    }

    override fun findById(unitId: String, spaceId: String, reserveDate: LocalDate): Uni<SpaceReserve?> {
        return client.preparedQuery("""
            SELECT 
                unit_id, space_id, reserve_date, CAST(reserve_time as text)
            FROM public.space_date_reserve
            WHERE unit_id = $1 AND space_id = $2 AND reserve_date = $3
        """.trimIndent()
        ).execute(Tuple.of(unitId, spaceId, reserveDate))
            .onItem().transform { it.iterator() }
            .onItem().transform {
                if(it.hasNext()){
                    val row = it.next()
                    SpaceReserve(
                        row.getString("unit_id"),
                        row.getString("space_id"),
                        row.getLocalDate("reserve_date"),
                        MinuteOfDate(
                            row.getString("reserve_time")
                        )
                    )
                }else{
                    null
                }
            }

    }
}