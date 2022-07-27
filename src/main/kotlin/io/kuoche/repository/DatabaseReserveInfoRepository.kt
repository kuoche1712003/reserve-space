package io.kuoche.repository

import io.kuoche.model.bo.ReserveInfo
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.SqlConnection
import io.vertx.mutiny.sqlclient.Tuple
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DatabaseReserveInfoRepository(
    private val client: PgPool
): ReserveInfoRepository {
    override fun add(reserveInfo: ReserveInfo, con: SqlConnection?): Uni<Int> {
        val sql = """
            INSERT INTO public.reserve_info (unit_id, space_id, date, start_time, end_time, email) 
            VALUES ($1, $2, $3, $4, $5, $6) RETURNING id
        """.trimIndent()
        val query = if(con == null){
            client.preparedQuery(sql)
        }else{
            con.preparedQuery(sql)
        }
        return query.execute(Tuple.of(reserveInfo.unitId, reserveInfo.spaceId, reserveInfo.date, reserveInfo.startTime, reserveInfo.endTime, reserveInfo.email))
            .map {
                it.iterator().next()
            }.map {
                it.getInteger("id")
            }
    }

    override fun findById(id: Int, con: SqlConnection?): Uni<ReserveInfo?> {
        val sql = """
            SELECT *
            FROM public.reserve_info
            WHERE id = $1
        """.trimIndent()
        val query = if(con == null){
            client.preparedQuery(sql)
        }else{
            con.preparedQuery(sql)
        }
        return query.execute(Tuple.of(id))
            .onItem().transform { it.iterator() }
            .onItem().transform {
                if(it.hasNext()){
                    val row = it.next()
                    ReserveInfo(
                        row.getInteger("id"),
                        row.getString("unit_id"),
                        row.getString("space_id"),
                        row.getLocalDate("date"),
                        row.getLocalTime("start_time"),
                        row.getLocalTime("end_time"),
                        row.getString("email")
                    )
                }else{
                    null
                }
            }
    }

    override fun remove(id: Int, con: SqlConnection?): Uni<Int> {
        val sql = """
            DELETE FROM public.reserve_info WHERE id = $1
        """.trimIndent()
        val query = if(con == null){
            client.preparedQuery(sql)
        }else{
            con.preparedQuery(sql)
        }
        return query.execute(Tuple.of(id))
            .map {
                it.rowCount()
            }
    }
}