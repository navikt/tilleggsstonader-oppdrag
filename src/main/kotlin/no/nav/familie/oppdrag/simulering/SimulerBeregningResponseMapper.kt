package no.nav.familie.oppdrag.simulering

import no.nav.system.os.entiteter.beregningskjema.BeregningsPeriode
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class SimulerBeregningResponseMapper {

    fun toSimulerResultDto(simulerBeregningResponse: SimulerBeregningResponse,
                           dato: LocalDate = LocalDate.now()): SimulerResultatDto {

        val totalEtterbetalingsBeløp = simulerBeregningResponse.response.simulering.beregningsPeriode.sumBy { finnEtterbetalingPerPeriode(it, dato) }

        return SimulerResultatDto(etterbetaling = totalEtterbetalingsBeløp)
    }

    private fun finnEtterbetalingPerPeriode(beregningsPeriode: BeregningsPeriode, dato: LocalDate): Int {
        // Fremtidige perioder gir ingen etterbetaling.
        val datoFraPeriode = LocalDate.parse(beregningsPeriode.periodeFom, DateTimeFormatter.ISO_DATE)
        if(datoFraPeriode.month > dato.month) return 0

        val stoppNivaBA =
                beregningsPeriode.beregningStoppnivaa.filter { it.kodeFagomraade == "BA"}

        // Feilutbetaling medfører at etterbetaling er 0
        val inneholderFeilUtbType = stoppNivaBA.any{ stopNivå -> stopNivå.beregningStoppnivaaDetaljer.any { detaljer -> detaljer.typeKlasse == "FEIL" } }
        if(inneholderFeilUtbType) return 0

        // Summer perioder av type YTEL og med forfallsdato bak i tiden.
        return stoppNivaBA.filter { førfallPassert(it.forfall, dato) }.flatMap { it.beregningStoppnivaaDetaljer }.filter { it.typeKlasse == "YTEL" }.sumBy { it.belop?.toInt() ?: 0}
    }

    private fun førfallPassert(forfall: String, dato: LocalDate): Boolean =
         dato >= LocalDate.parse(forfall, DateTimeFormatter.ISO_DATE)

}