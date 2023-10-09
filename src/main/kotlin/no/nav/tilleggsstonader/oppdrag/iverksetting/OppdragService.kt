package no.nav.tilleggsstonader.oppdrag.iverksetting

import no.nav.familie.kontrakter.felles.oppdrag.OppdragId
import no.nav.familie.kontrakter.felles.oppdrag.OppdragStatus
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.tilleggsstonader.oppdrag.domene.id
import no.nav.tilleggsstonader.oppdrag.repository.OppdragLager
import no.nav.tilleggsstonader.oppdrag.repository.OppdragLagerRepository
import no.trygdeetaten.skjema.oppdrag.Oppdrag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OppdragService(
    @Autowired private val oppdragSender: OppdragSender,
    @Autowired private val oppdragLagerRepository: OppdragLagerRepository,
) {

    @Transactional(rollbackFor = [Throwable::class])
    fun opprettOppdrag(utbetalingsoppdrag: Utbetalingsoppdrag, oppdrag: Oppdrag, versjon: Int) {
        LOG.debug("Lagrer oppdrag i databasen " + oppdrag.id)
        try {
            oppdragLagerRepository.opprettOppdrag(OppdragLager.lagFraOppdrag(utbetalingsoppdrag, oppdrag), versjon)
        } catch (e: org.springframework.dao.DuplicateKeyException) {
            LOG.info("Oppdrag ${oppdrag.id} er allerede sendt.")
            throw OppdragAlleredeSendtException()
        }

        LOG.debug("Legger oppdrag på kø " + oppdrag.id)
        oppdragSender.sendOppdrag(oppdrag)
    }

    fun hentStatusForOppdrag(oppdragId: OppdragId): OppdragLager {
        return oppdragLagerRepository.hentOppdrag(oppdragId)
    }

    @Transactional(rollbackFor = [Throwable::class])
    fun resendOppdrag(oppdragId: OppdragId) {
        val oppdrag = oppdragLagerRepository.hentOppdrag(oppdragId)
        if (oppdrag.status != OppdragStatus.KVITTERT_FUNKSJONELL_FEIL) {
            throw UnsupportedOperationException("Kan ikke resende $oppdragId då status=${oppdrag.status}")
        }
        LOG.info("Resender $oppdragId")
        val oppdragXml = Jaxb.tilOppdrag(oppdrag.utgåendeOppdrag)
        oppdragLagerRepository.oppdaterStatus(oppdragId, OppdragStatus.LAGT_PÅ_KØ)
        oppdragSender.sendOppdrag(oppdragXml)
    }

    companion object {

        val LOG = LoggerFactory.getLogger(OppdragService::class.java)
    }
}

class OppdragAlleredeSendtException() : RuntimeException()
