package io.kuoche

import com.fasterxml.jackson.annotation.JsonFormat
import io.kuoche.model.dto.ReserveInfoDTO
import io.kuoche.model.dto.ReserveSpaceDTO
import io.kuoche.model.dto.ReserveSpaceRequest
import io.kuoche.service.SpaceReserveService
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import java.time.LocalDate
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@Path("/space")
class ReserveSpaceResource(
    private val spaceReserveService: SpaceReserveService,
    private val logger: Logger
) {

    @GET
    @Path("/")
    suspend fun getReserveSpace(
        @QueryParam("unitId") unitId: String,
        @QueryParam("spaceId") spaceId: String,
        @QueryParam("reserveDate")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei")
        reserveDate: LocalDate
    ): ReserveSpaceDTO?{
        return spaceReserveService.getReserveSpace(unitId, spaceId, reserveDate)
    }

    @POST
    @Path("/reserve")
    suspend fun reserveSpace(request: ReserveSpaceRequest): Response{
        try{
            val result = spaceReserveService.reserveSpace(request.unitId, request.spaceId, request.reserveDate,
                request.startTime, request.endTime, request.email).awaitSuspending()
            return Response.ok(result).build()
        }catch (e: Exception){
            logger.warn(e.message)
        }
        return Response.status(400).build()
    }

    @DELETE
    @Path("/reserve/{id}")
    suspend fun cancelReserveSpace(@RestPath id: Int): Response{
        try{
            spaceReserveService.cancelReserveSpace(id).awaitSuspending()
            return Response.ok().build()
        }catch (e: Exception){
            logger.warn(e.message)
        }
        return Response.status(400).build()
    }
}