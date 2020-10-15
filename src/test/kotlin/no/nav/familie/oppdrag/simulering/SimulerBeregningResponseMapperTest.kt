package no.nav.familie.oppdrag.simulering

import no.nav.familie.oppdrag.simulering.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.Month


@ActiveProfiles("dev")
@SpringBootTest(classes = [SimulerBeregningResponseMapper::class],
                properties = ["spring.cloud.vault.enabled=false"])
class SimulerBeregningResponseMapperTest() {

    @Autowired lateinit var mapper: SimulerBeregningResponseMapper

    val dagensDato: LocalDate = LocalDate.of(2020, Month.SEPTEMBER, 15)

    @Test
    fun beregn_etterbetaling_foregaende_maned() {
        val enTideligereMåned = dagensDato.minusMonths(1)

        val periodeNåværendeMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppniva(dagensDato, 2)), dagensDato)

        val periodeTidligereMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppniva(enTideligereMåned)), enTideligereMåned)

        val response = lagSimulerBeregningResponse(listOf(periodeNåværendeMåned, periodeTidligereMåned))
        val dto = mapper.toRestSimulerResult(response, dagensDato)

        assertEquals(1000, dto.etterbetaling)
    }

    @Test
    fun bergen_etterbetaling_nåværende_og_foregaende_maned() {
        val enTideligereMåned = dagensDato.minusMonths(1)
        val enSenereMåned = dagensDato.plusMonths(1)

        val periodeNesteMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppniva(enSenereMåned)), enSenereMåned)

        val periodeNåværendeMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppniva(dagensDato)), dagensDato)

        val periodeTidligereMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppniva(enTideligereMåned)), enTideligereMåned)

        val response = lagSimulerBeregningResponse(listOf(periodeNesteMåned,
                                                          periodeNåværendeMåned, periodeTidligereMåned))
        val dto = mapper.toRestSimulerResult(response, dagensDato)

        assertEquals(2000, dto.etterbetaling)
    }

    @Test
    fun bergen_etterbetaling_med_revurdering() {
        val enTideligereMåned = dagensDato.minusMonths(1)

        val periodeNåværendeMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppniva(dagensDato, 2)), dagensDato)

        val periodeTidligereMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppnivaRevurdering(enTideligereMåned)), enTideligereMåned)

        val response = lagSimulerBeregningResponse(listOf(periodeNåværendeMåned, periodeTidligereMåned))
        val dto = mapper.toRestSimulerResult(response, dagensDato)

        assertEquals(500, dto.etterbetaling)
    }

    @Test
    fun bergen_etterbetaling_med_feilutbetaling() {
        val enTideligereMåned = dagensDato.minusMonths(1)

        val periodeNåværendeMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppniva(dagensDato, 2)), dagensDato)

        val periodeTidligereMåned = lagBeregningsPeriode(
                listOf(lagBeregningStoppnivaFeilUtbetaling(enTideligereMåned)), enTideligereMåned)

        val response = lagSimulerBeregningResponse(listOf(periodeNåværendeMåned, periodeTidligereMåned))
        val dto = mapper.toRestSimulerResult(response, dagensDato)

        assertEquals(0, dto.etterbetaling)
    }
}