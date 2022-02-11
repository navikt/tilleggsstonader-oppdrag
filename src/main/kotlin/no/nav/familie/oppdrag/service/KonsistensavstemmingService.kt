package no.nav.familie.oppdrag.service

import no.nav.familie.kontrakter.felles.oppdrag.KonsistensavstemmingRequestV2
import no.nav.familie.kontrakter.felles.oppdrag.KonsistensavstemmingUtbetalingsoppdrag
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.oppdrag.avstemming.AvstemmingSender
import no.nav.familie.oppdrag.konsistensavstemming.KonsistensavstemmingMapper
import no.nav.familie.oppdrag.repository.OppdragLagerRepository
import no.nav.familie.oppdrag.repository.UtbetalingsoppdragForKonsistensavstemming
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import java.util.Objects.isNull

@Service
class KonsistensavstemmingService(
    private val avstemmingSender: AvstemmingSender,
    private val oppdragLagerRepository: OppdragLagerRepository,
    private val mellomlagringKonsistensavstemmingService: MellomlagringKonsistensavstemmingService,
) {

    @Transactional
    fun utførKonsistensavstemming(request: KonsistensavstemmingUtbetalingsoppdrag) {
        utførKonsistensavstemming(request.fagsystem, request.utbetalingsoppdrag, request.avstemmingstidspunkt)
    }

    private fun utførKonsistensavstemming(
        fagsystem: String,
        utbetalingsoppdrag: List<Utbetalingsoppdrag>,
        avstemmingstidspunkt: LocalDateTime,
        sendStartmelding: Boolean = true,
        sendAvsluttmelding: Boolean = true,
        transaksjonsId: String? = null
    ) {
        val metaInfo = KonsistensavstemmingMetaInfo(
            Fagsystem.valueOf(fagsystem), transaksjonsId, avstemmingstidspunkt, sendStartmelding,
            sendAvsluttmelding, utbetalingsoppdrag
        )

        if (metaInfo.erFørsteBatchIEnSplittetBatch()) {
            mellomlagringKonsistensavstemmingService.sjekkAtDetteErFørsteMelding(metaInfo.transaksjonsId!!)
        }

        val konsistensavstemmingMapper = opprettKonsistensavstemmingMapper(metaInfo)

        val meldinger = konsistensavstemmingMapper.lagAvstemmingsmeldinger()

        if (meldinger.isEmpty()) {
            LOG.info("Ingen oppdrag å utføre konsistensavstemming for")
            return
        }

        LOG.info(
            "Utfører konsistensavstemming for id ${konsistensavstemmingMapper.avstemmingId} antall meldinger er ${meldinger.size}"
        )
        meldinger.forEach {
            avstemmingSender.sendKonsistensAvstemming(it)
        }

        if (metaInfo.erSplittetBatchMenIkkeSisteBatch()) {
            mellomlagringKonsistensavstemmingService.opprettInnslagIMellomlagring(metaInfo,
                                                                                  konsistensavstemmingMapper.antallOppdrag,
                                                                                  konsistensavstemmingMapper.totalBeløp
            )
        }
        LOG.info("Fullført konsistensavstemming for id ${konsistensavstemmingMapper.avstemmingId}")
    }

    @Transactional
    fun utførKonsistensavstemming(
        request: KonsistensavstemmingRequestV2,
        sendStartMelding: Boolean,
        sendAvsluttmelding: Boolean,
        transaksjonsId: String?
    ) {
        sjekkAtTransaktionsIdErSattHvisSplittetBatch(sendStartMelding, sendAvsluttmelding, transaksjonsId)

        val fagsystem = request.fagsystem
        val avstemmingstidspunkt = request.avstemmingstidspunkt

        val perioderPåBehandling = request.perioderForBehandlinger.map { it.behandlingId to it.perioder }.toMap()
        verifyUnikeBehandlinger(perioderPåBehandling, request)

        val utbetalingsoppdragForKonsistensavstemming =
            oppdragLagerRepository.hentUtbetalingsoppdragForKonsistensavstemming(fagsystem, perioderPåBehandling.keys)

        val utbetalingsoppdrag = leggAktuellePerioderISisteUtbetalingsoppdraget(
            utbetalingsoppdragForKonsistensavstemming,
            perioderPåBehandling
        )

        utførKonsistensavstemming(fagsystem, utbetalingsoppdrag, avstemmingstidspunkt, sendStartMelding, sendAvsluttmelding, transaksjonsId)
    }

    private fun sjekkAtTransaktionsIdErSattHvisSplittetBatch(sendStartMelding: Boolean, sendAvsluttmelding: Boolean, transaksjonsId: String?) {
        if (!(sendStartMelding && sendAvsluttmelding) && isNull(transaksjonsId)) {
            throw Exception("Er sendStartmelding eller sendAvsluttmelding satt til false må transaksjonsId være definert.")
        }
    }

    private fun opprettKonsistensavstemmingMapper(
        metaInfo: KonsistensavstemmingMetaInfo,
    ): KonsistensavstemmingMapper {
        val aggregertAntallOppdrag = mellomlagringKonsistensavstemmingService.hentAggregertAntallOppdrag(metaInfo)
        val aggregertTotalBeløp = mellomlagringKonsistensavstemmingService.hentAggregertBeløp(metaInfo)

        return KonsistensavstemmingMapper(
            fagsystem = metaInfo.fagsystem.name,
            utbetalingsoppdrag = metaInfo.utbetalingsoppdrag,
            avstemmingsDato = metaInfo.avstemmingstidspunkt,
            sendStartmelding = metaInfo.sendStartmelding,
            sendAvsluttmelding = metaInfo.sendAvsluttmelding,
            aggregertAntallOppdrag = aggregertAntallOppdrag,
            aggregertTotalBeløp = aggregertTotalBeløp,
            transaksjonsId = metaInfo.transaksjonsId?.let { UUID.fromString(it) },
        )
    }

    /**
     * Legger inn alle (filtrerte) perioder for en gitt fagsak i det siste utbetalingsoppdraget
     */
    private fun leggAktuellePerioderISisteUtbetalingsoppdraget(
        utbetalingsoppdrag: List<UtbetalingsoppdragForKonsistensavstemming>,
        perioderPåBehandling: Map<String, Set<Long>>,
    ): List<Utbetalingsoppdrag> {
        val utbetalingsoppdragPåFagsak = utbetalingsoppdrag.groupBy { it.fagsakId }

        return utbetalingsoppdragPåFagsak.map { (saksnummer, utbetalingsoppdragListe) ->
            val senesteUtbetalingsoppdrag = utbetalingsoppdragListe.maxByOrNull { oppdrag ->
                oppdrag.utbetalingsoppdrag.utbetalingsperiode.maxOf { it.periodeId }
            }?.utbetalingsoppdrag ?: error("Finner ikke seneste behandling for fagsak=$saksnummer")

            val behandlingsIderForFagsak = utbetalingsoppdragListe.map { it.behandlingId }.toSet()

            val aktuellePeriodeIderForFagsak =
                perioderPåBehandling.filter { behandlingsIderForFagsak.contains(it.key) }.values.flatten()

            val perioderTilKonsistensavstemming = utbetalingsoppdragListe.flatMap {
                it.utbetalingsoppdrag.utbetalingsperiode
                    .filter { utbetalingsperiode -> aktuellePeriodeIderForFagsak.contains(utbetalingsperiode.periodeId) }
            }

            senesteUtbetalingsoppdrag.copy(utbetalingsperiode = perioderTilKonsistensavstemming)
        }
    }

    private fun verifyUnikeBehandlinger(periodeIderPåBehandling: Map<String, Set<Long>>, request: KonsistensavstemmingRequestV2) {
        if (periodeIderPåBehandling.size != request.perioderForBehandlinger.size) {
            val duplikateBehandlinger =
                request.perioderForBehandlinger.map { it.behandlingId }.groupingBy { it }.eachCount().filter { it.value > 1 }
            error("Behandling finnes flere ganger i requesten: ${duplikateBehandlinger.keys}")
        }
    }

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(KonsistensavstemmingService::class.java)
    }
}
