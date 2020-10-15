package no.nav.familie.oppdrag.simulering

import no.nav.familie.kontrakter.felles.oppdrag.RestSimulerResultat
import no.nav.familie.oppdrag.rest.SimuleringController
import no.nav.system.os.entiteter.beregningskjema.BeregningsPeriode
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class SimulerBeregningResponseMapper {

    fun toRestSimulerResult(simulerBeregningResponse: SimulerBeregningResponse,
                            dato: LocalDate = LocalDate.now()): RestSimulerResultat {

        val totalEtterbetalingsBeløp =
                simulerBeregningResponse.response.simulering.beregningsPeriode.sumBy { finnEtterbetalingPerPeriode(it, dato) }

        return RestSimulerResultat(etterbetaling = totalEtterbetalingsBeløp)
    }

    private fun finnEtterbetalingPerPeriode(beregningsPeriode: BeregningsPeriode, dato: LocalDate): Int {
        LOG.info("Finn etterbetalingsperiode for periode: ${beregningsPeriode.periodeFom} ")

        // Fremtidige perioder gir ingen etterbetaling.
        val datoFraPeriode = LocalDate.parse(beregningsPeriode.periodeFom, DateTimeFormatter.ISO_DATE)
        if (datoFraPeriode.month > dato.month) return 0

        val stoppNivaBA =
                beregningsPeriode.beregningStoppnivaa.filter { it.kodeFagomraade?.trim() == "BA" }

        // Feilutbetaling medfører at etterbetaling er 0
        val inneholderFeilUtbType =
                stoppNivaBA.any { stopNivå -> stopNivå.beregningStoppnivaaDetaljer.any { detaljer -> detaljer.typeKlasse?.trim() == TypeKlasse.FEIL.name } }
        if (inneholderFeilUtbType) return 0

        // Summer perioder av type YTEL og med forfallsdato bak i tiden.
        val sum = stoppNivaBA.filter { førfallPassert(it.forfall, dato) }
                .flatMap { it.beregningStoppnivaaDetaljer }
                .filter { it.typeKlasse?.trim() == TypeKlasse.YTEL.name }
                .sumBy { it.belop?.toInt() ?: 0 }
        LOG.info("Sum etterbetaling for perioden er $sum")
        return sum
    }

    private fun førfallPassert(forfall: String, dato: LocalDate): Boolean =
            dato >= LocalDate.parse(forfall, DateTimeFormatter.ISO_DATE)

    companion object {

        val LOG = LoggerFactory.getLogger(SimulerBeregningResponseMapper::class.java)
    }
}

enum class TypeKlasse {
    FEIL,
    YTEL
}
