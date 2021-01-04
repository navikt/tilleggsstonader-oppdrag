package no.nav.familie.oppdrag.service

import no.nav.familie.kontrakter.felles.oppdrag.*
import no.nav.familie.oppdrag.avstemming.AvstemmingSender
import no.nav.familie.oppdrag.konsistensavstemming.KonsistensavstemmingMapper
import no.nav.familie.oppdrag.repository.OppdragLagerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class KonsistensavstemmingService(
        private val avstemmingSender: AvstemmingSender,
        private val oppdragLagerRepository: OppdragLagerRepository,
) {

    @Deprecated("Bruk KonsistensavstemmingRequestV2")
    fun utførKonsistensavstemming(request: KonsistensavstemmingRequest) {
        val oppdragIdListe = request.oppdragIdListe
        val fagsystem = request.fagsystem
        val avstemmingstidspunkt = request.avstemmingstidspunkt

        val utbetalingsoppdrag = oppdragIdListe.map { id ->
            val oppdragLager = oppdragLagerRepository.hentAlleVersjonerAvOppdrag(OppdragId(fagsystem,
                                                                                           id.personIdent,
                                                                                           id.behandlingsId.toString()))
                    .find { oppdragLager -> oppdragLager.status == OppdragStatus.KVITTERT_OK
                                            || oppdragLager.status == OppdragStatus.KVITTERT_MED_MANGLER }
            oppdragLagerRepository.hentUtbetalingsoppdrag(OppdragId(fagsystem, id.personIdent, id.behandlingsId.toString()),
                                                          oppdragLager!!.versjon)
        }

        utførKonsistensavstemming(fagsystem, utbetalingsoppdrag, avstemmingstidspunkt)
    }

    private fun utførKonsistensavstemming(
            fagsystem: String,
            utbetalingsoppdrag: List<Utbetalingsoppdrag>,
            avstemmingstidspunkt: LocalDateTime,
    ) {
        val konsistensavstemmingMapper = KonsistensavstemmingMapper(fagsystem, utbetalingsoppdrag, avstemmingstidspunkt)
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

        val periodeIdnPaaFagsak = request.periodeIdn.map { it.fagsakId to it.periodeIdn }.toMap()
        verifyUnikeFagsaker(periodeIdnPaaFagsak, request)

        val utbetalingsoppdragPaaFagsak = request.periodeIdn.mapNotNull { id ->
            if (id.periodeIdn.isNotEmpty()) {
                val utbetalingsoppdrag = oppdragLagerRepository.hentUtbetalingsoppdragForKonsistensavstemming(fagsystem,
                                                                                                              id.fagsakId,
                                                                                                              id.periodeIdn)
                id.fagsakId to utbetalingsoppdrag
            } else {
                LOG.warn("Sendt en tom liste for fagsak={}", id.fagsakId)
                null
            }
        }

        val utbetalingsoppdrag = leggAktuellePerioderISisteUtbetalingsoppdraget(utbetalingsoppdragPaaFagsak, periodeIdnPaaFagsak)

        utførKonsistensavstemming(fagsystem, utbetalingsoppdrag, avstemmingstidspunkt)
    }

    private fun leggAktuellePerioderISisteUtbetalingsoppdraget(
            utbetalingsoppdragPaaFagsak: List<Pair<String, List<Utbetalingsoppdrag>>>,
            periodeIdnPaaFagsak: Map<String, Set<Long>>,
    ): List<Utbetalingsoppdrag> {
        return utbetalingsoppdragPaaFagsak.map { (saksnummer, utbetalingsoppdragListe) ->
            val periodeIdn = periodeIdnPaaFagsak[saksnummer] ?: error("Finner ikke periodeIdn for fagsak=$saksnummer")
            val senesteUtbetalingsoppdrag =
                    utbetalingsoppdragListe.maxByOrNull { oppdrag -> oppdrag.utbetalingsperiode.maxOf { it.periodeId } }!!
            val perioderTilKonsistensavstemming =
                    utbetalingsoppdragListe.flatMap { it.utbetalingsperiode.filter { periodeIdn.contains(it.periodeId) } }
            senesteUtbetalingsoppdrag.copy(utbetalingsperiode = perioderTilKonsistensavstemming)
        }
    }

    private fun verifyUnikeFagsaker(periodeIdnPaaFagsak: Map<String, Set<Long>>, request: KonsistensavstemmingRequestV2) {
        if (periodeIdnPaaFagsak.size != request.periodeIdn.size) {
            val duplikateFagsaker = request.periodeIdn.map { it.fagsakId }.groupingBy { it }.eachCount().filter { it.value > 1 }
            error("Fagsaker finnes flere ganger i requesten: ${duplikateFagsaker.keys}")
        }
    }

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(KonsistensavstemmingService::class.java)
    }
}