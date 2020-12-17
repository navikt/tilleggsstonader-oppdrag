package no.nav.familie.oppdrag.service

import no.nav.familie.kontrakter.felles.oppdrag.*
import no.nav.familie.oppdrag.avstemming.AvstemmingSender
import no.nav.familie.oppdrag.konsistensavstemming.KonsistensavstemmingMapper
import no.nav.familie.oppdrag.repository.OppdragLagerRepository
import no.nav.familie.oppdrag.rest.KonsistensavstemmingRequestV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class KonsistensavstemmingService(private val avstemmingSender: AvstemmingSender,
                                  private val oppdragLagerRepository: OppdragLagerRepository) {

    @Deprecated("Bruk KonsistensavstemmingRequestV2")
    fun utførKonsistensavstemming(request: KonsistensavstemmingRequest) {
        utførKonsistensavstemming(request.fagsystem,
                                  request.oppdragIdListe,
                                  request.avstemmingstidspunkt)
    }

    @Deprecated("Bruk KonsistensavstemmingRequestV2")
    fun utførKonsistensavstemming(fagsystem: String,
                                  oppdragIdListe: List<OppdragIdForFagsystem>,
                                  avstemmingstidspunkt: LocalDateTime) {

        val utbetalingsoppdrag = oppdragIdListe.map { id ->
            val oppdragLager = oppdragLagerRepository.hentAlleVersjonerAvOppdrag(OppdragId(fagsystem,
                                                                                           id.personIdent,
                                                                                           id.behandlingsId.toString()))
                    .find { oppdragLager -> oppdragLager.status == OppdragStatus.KVITTERT_OK
                                            || oppdragLager.status == OppdragStatus.KVITTERT_MED_MANGLER }
            oppdragLagerRepository.hentUtbetalingsoppdrag(OppdragId(fagsystem, id.personIdent, id.behandlingsId.toString()),
                                                          oppdragLager!!.versjon)
        }

        val konsistensavstemmingMapper = KonsistensavstemmingMapper(fagsystem, utbetalingsoppdrag, emptyList(), avstemmingstidspunkt)
        val meldinger = konsistensavstemmingMapper.lagAvstemmingsmeldinger()

        if (meldinger.isEmpty()) {
            LOG.info("Ingen oppdrag å utføre konsistensavstemming for")
            return
        }

        LOG.info("Utfører konsistensavstemming for id ${konsistensavstemmingMapper.avstemmingId}, " +
                 "antall meldinger er ${meldinger.size}")
        meldinger.forEach {
            avstemmingSender.sendKonsistensAvstemming(it)
        }

        LOG.info("Fullført konsistensavstemming for id ${konsistensavstemmingMapper.avstemmingId}")
    }

    fun utførKonsistensavstemming(request: KonsistensavstemmingRequestV2) {
        val fagsystem = request.fagsystem
        val avstemmingstidspunkt = request.avstemmingstidspunkt
        val utbetalingsoppdrag = request.periodeIdn.map { id ->
            oppdragLagerRepository.hentUtbetalingsoppdragForKonsistensavstemming(fagsystem, id.fagsakId, id.periodeIdn)
        }.flatten()

        val konsistensavstemmingMapper =
                KonsistensavstemmingMapper(fagsystem, utbetalingsoppdrag, request.periodeIdn, avstemmingstidspunkt)
        val meldinger = konsistensavstemmingMapper.lagAvstemmingsmeldingerV2()

        if (meldinger.isEmpty()) {
            LOG.info("Ingen oppdrag å utføre konsistensavstemming for")
            return
        }

        LOG.info("Utfører konsistensavstemming for id ${konsistensavstemmingMapper.avstemmingId}, " +
                 "antall meldinger er ${meldinger.size}")
        meldinger.forEach {
            avstemmingSender.sendKonsistensAvstemming(it)
        }

        LOG.info("Fullført konsistensavstemming for id ${konsistensavstemmingMapper.avstemmingId}")
    }

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(KonsistensavstemmingService::class.java)
    }
}