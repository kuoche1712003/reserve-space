package io.kuoche.repository

import io.kuoche.model.bo.MinuteOfDate
import io.kuoche.model.bo.SpaceReserve
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.SqlConnection
import java.time.LocalDate

interface SpaceReserveRepository {
    fun add(spaceReserve: SpaceReserve): Uni<Int>
    fun reserveById(minuteOfDate: MinuteOfDate, unitId: String, spaceId: String, reserveDate: LocalDate, con: SqlConnection? = null): Uni<Int>
    fun cancelReserveById(minuteOfDate: MinuteOfDate, unitId: String, spaceId: String, reserveDate: LocalDate, con: SqlConnection? = null): Uni<Int>
    fun findById(unitId: String, spaceId: String, reserveDate: LocalDate): Uni<SpaceReserve?>
}