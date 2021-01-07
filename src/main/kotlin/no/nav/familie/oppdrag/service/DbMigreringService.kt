package no.nav.familie.oppdrag.service

import no.nav.familie.kontrakter.felles.oppdrag.OppdragStatus
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsperiode
import no.nav.familie.oppdrag.repository.OppdragLager
import no.nav.familie.oppdrag.repository.OppdragLagerRepository
import no.nav.familie.oppdrag.repository.OppdragRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class DbMigreringService(private val oppdragRepository: OppdragRepository,
                         private val oppdragLagerRepository: OppdragLagerRepository) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(initialDelay = 12000, fixedDelay = 60000)
    @Transactional
    fun dbMigrering() {


//        oppdragLagerRepository.opprettOppdrag(OppdragLager(UUID.randomUUID(),
//                                              "lkjh",
//                                              "654",
//                                              "54",
//                                              "21",
//                                              Utbetalingsoppdrag(Utbetalingsoppdrag.KodeEndring.NY,
//                                                                 "54",
//                                                                 "sqwd",
//                                                                 "wqd",
//                                                                 "swqlkj",
//                                                                 LocalDateTime.now(),
//                                                                 listOf(Utbetalingsperiode(false,
//                                                                                           null,
//                                                                                           12L,
//                                                                                           null,
//                                                                                           LocalDate.now(),
//                                                                                           "lijkuhg",
//                                                                                           LocalDate.now(),
//                                                                                           LocalDate.now(),
//                                                                                           BigDecimal.ONE,
//                                                                                           Utbetalingsperiode.SatsType.DAG,
//                                                                                           "io",
//                                                                                           15))
//                                              ),
//                                              "",
//                                              OppdragStatus.LAGT_PÅ_KØ,
//                                              LocalDateTime.now(),
//                                              LocalDateTime.now(),
//                                              "654",
//                                              2),
//                                              2)

        val iterable = oppdragRepository.findWhereUuidIsNull()

        if (iterable.none()) {
            logger.info("Migrering for uuid fullført.")
            return
        }


        iterable.forEach { oppdragRepository.updateUuid(it.behandlingId, it.personIdent, it.fagsystem, it.versjon) }
        logger.info("Migrert  ${iterable.count()} oppdraglager for uuid.")

    }

}
