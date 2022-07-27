package io.kuoche.model.bo

import java.time.LocalDate

class SpaceReserve(
    val unitId: String,
    val spaceId: String,
    val reserveDate: LocalDate,
    val reserveTime: MinuteOfDate
) {

}