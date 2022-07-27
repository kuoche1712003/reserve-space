package io.kuoche.service

import io.kuoche.model.dto.ReserveInfoDTO
import io.kuoche.model.dto.ReserveSpaceDTO
import io.smallrye.mutiny.Uni
import java.time.LocalDate
import java.time.LocalTime

interface SpaceReserveService {
    fun reserveSpace(unitId: String, spaceId: String, reserveDate: LocalDate, start: LocalTime, end: LocalTime, email: String): Uni<ReserveInfoDTO>
    fun cancelReserveSpace(id: Int): Uni<Int>
    suspend fun getReserveSpace(unitId: String, spaceId: String, reserveDate: LocalDate): ReserveSpaceDTO?
}