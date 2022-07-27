package io.kuoche.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalTime

data class ReserveSpaceDTO(
    var unitId: String,

    var spaceId: String,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei")
    var reserveDate: LocalDate,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Taipei")
    var emptyTime: List<List<LocalTime>>
)
