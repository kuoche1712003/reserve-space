package io.kuoche.service

import io.kuoche.model.bo.MinuteOfDate
import io.kuoche.model.bo.ReserveInfo
import io.kuoche.model.dto.ReserveInfoDTO
import io.kuoche.model.dto.ReserveSpaceDTO
import io.kuoche.repository.ReserveInfoRepository
import io.kuoche.repository.SpaceReserveRepository
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import java.time.LocalDate
import java.time.LocalTime
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DefaultSpaceReserveService(
    private val spaceReserveRepository: SpaceReserveRepository,
    private val reserveInfoRepository: ReserveInfoRepository,
    private val client: PgPool
): SpaceReserveService {

    override fun reserveSpace(
        unitId: String,
        spaceId: String,
        reserveDate: LocalDate,
        start: LocalTime,
        end: LocalTime,
        email: String
    ): Uni<ReserveInfoDTO> {
        val minuteOfDate = MinuteOfDate(start, end)
        return client.withTransaction {con ->
            spaceReserveRepository.reserveById(minuteOfDate, unitId, spaceId, reserveDate, con)
                .onItem().transformToUni { rows ->
                    if(rows == 0){
                        throw RuntimeException("預約時間已滿")
                    }
                    val info = ReserveInfo(null, unitId, spaceId, reserveDate, start, end, email)
                    reserveInfoRepository.add(info, con)
                }
                .onItem().transform{id ->
                    ReserveInfoDTO(id, unitId, spaceId, reserveDate.atTime(start), reserveDate.atTime(end), email)
                }
        }
    }

    override fun cancelReserveSpace(
        reserveId: Int
    ):Uni<Int> {
        return client.withTransaction {con->
            reserveInfoRepository.findById(reserveId, con)
                .onItem().transformToUni { info ->
                    if(info == null){
                        throw RuntimeException("無此時段預約")
                    }
                    val minuteOfDate = MinuteOfDate(info.startTime, info.endTime)
                    spaceReserveRepository.cancelReserveById(minuteOfDate, info.unitId, info.spaceId, info.date, con)
                }
                .onItem().transformToUni { rows ->
                    if(rows == 0){
                        throw RuntimeException("無此時段預約")
                    }

                    reserveInfoRepository.remove(reserveId, con)
                    throw RuntimeException("123")
                }
        }
    }

    override suspend fun getReserveSpace(unitId: String, spaceId: String, reserveDate: LocalDate): ReserveSpaceDTO? {
        return spaceReserveRepository.findById(unitId, spaceId, reserveDate).awaitSuspending()
            ?.let {
                ReserveSpaceDTO(it.unitId, it.spaceId, it.reserveDate, it.reserveTime.emptyTime())
            }
    }
}
