package io.kuoche.repository

import io.kuoche.model.bo.ReserveInfo
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.SqlConnection

interface ReserveInfoRepository {
    fun add(reserveInfo: ReserveInfo, con: SqlConnection? = null): Uni<Int>
    fun findById(id: Int, con: SqlConnection? = null): Uni<ReserveInfo?>
    fun remove(id: Int, con: SqlConnection? = null): Uni<Int>
}