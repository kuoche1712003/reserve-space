package io.kuoche.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalTime

data class ReserveSpaceRequest(
    var unitId: String,

    var spaceId: String,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei")
    var reserveDate: LocalDate,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Taipei")
    var startTime: LocalTime,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Taipei")
    var endTime: LocalTime,

    val email: String
)
