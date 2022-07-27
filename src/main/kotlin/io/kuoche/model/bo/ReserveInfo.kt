package io.kuoche.model.bo

import java.time.LocalDate
import java.time.LocalTime

class ReserveInfo(
    val id: Int?,
    val unitId: String,
    val spaceId: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val email: String
) {
}