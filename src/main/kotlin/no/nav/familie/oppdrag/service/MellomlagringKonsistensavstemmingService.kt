package no.nav.familie.oppdrag.service

import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.oppdrag.repository.MellomlagringKonsistensavstemming
import no.nav.familie.oppdrag.repository.MellomlagringKonsistensavstemmingRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MellomlagringKonsistensavstemmingService(
    private val mellomlagringKonsistensavstemmingRepository: MellomlagringKonsistensavstemmingRepository,
) {

    fun nullstillMellomlagring(
        metaInfo: KonsistensavstemmingMetaInfo
    ) {
        val deaktivertMellomlagring =
            mellomlagringKonsistensavstemmingRepository.findAllByFagsystemAndAvstemmingstidspunktAndAktiv(
                metaInfo.fagsystem,
                metaInfo.avstemmingstidspunkt.format(
                    MellomlagringKonsistensavstemming.avstemingstidspunktFormater
                ),
                true
            ).map { mk -> mk.also { it.aktiv = false } }

        mellomlagringKonsistensavstemmingRepository.updateAll(deaktivertMellomlagring)

        LOG.info("Nullstilt mellomlagring for avstemmingstidspunkt ${metaInfo.avstemmingstidspunkt}")
    }

    fun hentAggregertBeløp(
        metaInfo: KonsistensavstemmingMetaInfo
    ): Long =
        if (metaInfo.erSisteBatchIEnSplittetBatch()) {
            mellomlagringKonsistensavstemmingRepository.hentAggregertTotalBeløp(
                metaInfo.fagsystem,
                metaInfo.avstemmingstidspunkt.format(MellomlagringKonsistensavstemming.avstemingstidspunktFormater)
            )
        } else {
            0L
        }

    fun hentAggregertAntallOppdrag(
        metaInfo: KonsistensavstemmingMetaInfo
    ): Int {
        return if (metaInfo.erSisteBatchIEnSplittetBatch()) {
            mellomlagringKonsistensavstemmingRepository.hentAggregertAntallOppdrag(
                metaInfo.fagsystem,
                metaInfo.avstemmingstidspunkt.format(MellomlagringKonsistensavstemming.avstemingstidspunktFormater)
            )
        } else {
            0
        }
    }

    fun opprettInnslagIMellomlagring(
        metaInfo: KonsistensavstemmingMetaInfo,
        antalOppdrag: Int,
        totalBeløp: Long,
    ) {
        val mellomlagring = MellomlagringKonsistensavstemming(
            fagsystem = metaInfo.fagsystem,
            avstemmingstidspunkt = metaInfo.avstemmingstidspunkt.format(MellomlagringKonsistensavstemming.avstemingstidspunktFormater),
            antallOppdrag = antalOppdrag,
            totalBeløp = totalBeløp,
        )
        mellomlagringKonsistensavstemmingRepository.insert(mellomlagring)
        LOG.info("Opprettet mellomlagring for avstemmingstidspunkt ${metaInfo.avstemmingstidspunkt}")
    }

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(MellomlagringKonsistensavstemmingService::class.java)
    }
}

data class KonsistensavstemmingMetaInfo(
    val fagsystem: Fagsystem,
    val avstemmingstidspunkt: LocalDateTime,
    val sendStartmelding: Boolean,
    val sendAvsluttmelding: Boolean,
    val utbetalingsoppdrag: List<Utbetalingsoppdrag>,
) {

    fun erFørsteBatchIEnSplittetBatch(): Boolean = sendStartmelding && !sendAvsluttmelding
    fun erSisteBatchIEnSplittetBatch(): Boolean = !sendStartmelding && sendAvsluttmelding
    fun erSplittetBatchMenIkkeSisteBatch(): Boolean = erSplittetBatch() && !erSisteBatchIEnSplittetBatch()
    fun erSplittetBatch(): Boolean = !sendStartmelding || !sendAvsluttmelding
}
