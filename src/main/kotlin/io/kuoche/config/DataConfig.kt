package io.kuoche.config

import io.kuoche.model.bo.SpaceReserve
import io.kuoche.model.bo.MinuteOfDate
import io.kuoche.repository.SpaceReserveRepository
import io.quarkus.runtime.StartupEvent
import io.vertx.mutiny.pgclient.PgPool
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.time.LocalDate
import java.time.LocalTime
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class DataConfig(
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    val schemaCreate: Boolean,
    val client: PgPool,
    val spaceReserveRepository: SpaceReserveRepository,
    val logger: Logger
) {

    fun config(@Observes ev: StartupEvent){
        if(schemaCreate){
            initSchema()
            initData()
        }
    }

    fun initSchema(){
        logger.info("schema")
        client.query("""
            CREATE TABLE IF NOT EXISTS public.space_date_reserve (
            	unit_id varchar(50) NOT NULL,
            	space_id varchar(50) NOT NULL,
            	reserve_date date NOT NULL,
            	reserve_time bit varying(1440) NOT NULL,
            	CONSTRAINT space_date_reserve_pk PRIMARY KEY (unit_id,space_id,reserve_date)
            )
            TABLESPACE pg_default;
        """.trimIndent()).execute().await().indefinitely()
        client.query("""
            CREATE TABLE IF NOT EXISTS public.reserve_info(
            	id SERIAL PRIMARY KEY,
                unit_id varchar(50) NOT NULL,
            	space_id varchar(50) NOT NULL,
                date date NOT NULL,
                start_time time NOT NULL,
                end_time time NOT NULL,
                email varchar(250) NOT NULL
            )
            TABLESPACE pg_default;
        """.trimIndent()).execute().await().indefinitely()
    }

    fun initData(){
        logger.info("data")
        val time = MinuteOfDate(LocalTime.of(9,0), LocalTime.of(18,0))
        time.flip(0, MinuteOfDate.LENGTH)
        val spaceReserve = SpaceReserve(
            "unit001",
            "space001",
            LocalDate.of(2022,1,1),
            time
        )
        spaceReserveRepository.findById(spaceReserve.unitId, spaceReserve.spaceId, spaceReserve.reserveDate)
            .subscribe().with {
                if(it == null){
                    spaceReserveRepository.add(spaceReserve)
                        .subscribe().with {
                            logger.info("space_date_reserve update rows $it")
                        }
                }

            }

    }

}