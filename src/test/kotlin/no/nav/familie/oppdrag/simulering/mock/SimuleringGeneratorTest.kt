package no.nav.familie.oppdrag.simulering.mock

import no.nav.system.os.entiteter.typer.simpletypes.KodeStatusLinje
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdrag
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdragslinje
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class SimuleringGeneratorTest {
    var simuleringGenerator = SimuleringGenerator()
    @Test
    fun SimuleringTestPositiv() {
        val response = simuleringGenerator.opprettSimuleringsResultat(simTestDataBaNy())
        Assertions.assertThat(response.response.simulering.gjelderId).isEqualTo("12345678901")
        Assertions.assertThat(response.response.simulering.beregningsPeriode.size).isEqualTo(1)
        Assertions.assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa.size).isEqualTo(5)
    }
    @Test
    fun SimuleringTestReduksjon() {
        val response = simuleringGenerator.opprettSimuleringsResultat(simTestDataBaReduksjon())
        Assertions.assertThat(response.response.simulering.gjelderId).isEqualTo("12345678902")
        Assertions.assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer.size).isEqualTo(3)
        Assertions.assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer[1].typeKlasse).isEqualTo("FEIL")
        Assertions.assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer[1].belop).isEqualTo(BigDecimal.valueOf(130))
    }
    @Test
    fun SimuleringTestOpphør() {
        val response = simuleringGenerator.opprettSimuleringsResultat(simTestDataBaOpphør())
        Assertions.assertThat(response.response.simulering.gjelderId).isEqualTo("12345678903")
        Assertions.assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer[1].typeKlasse).isEqualTo("FEIL")
        Assertions.assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer[1].belop).isEqualTo(BigDecimal.valueOf(1330))
    }

    private fun simTestDataBaNy(): SimulerBeregningRequest {
        val request = SimulerBeregningRequest()
        request.request =
            no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest()
        request.request.oppdrag = Oppdrag()
        request.request.oppdrag.kodeEndring = "NY"
        request.request.oppdrag.kodeFagomraade = "BA"
        request.request.oppdrag.fagsystemId = "123456789"
        request.request.oppdrag.oppdragGjelderId = "12345678901"
        request.request.oppdrag.saksbehId = "saksbeh"
        val oppdragslinje = Oppdragslinje()
        oppdragslinje.kodeEndringLinje = "NY"
        oppdragslinje.vedtakId = "2020-11-27"
        oppdragslinje.delytelseId = "1122334455667700"
        oppdragslinje.kodeKlassifik = "FPADATORD"
        oppdragslinje.datoVedtakFom = "2020-07-27"
        oppdragslinje.datoVedtakTom = "2020-11-08"
        oppdragslinje.datoStatusFom = "2020-10-19"
        oppdragslinje.sats = BigDecimal.valueOf(2339)
        oppdragslinje.typeSats = "MND"
        oppdragslinje.saksbehId = "saksbeh"
        oppdragslinje.utbetalesTilId = "12345678901"
        oppdragslinje.henvisning = "123456"
        request.request.oppdrag.oppdragslinje.add(oppdragslinje)
        return request
    }

    private fun simTestDataBaReduksjon(): SimulerBeregningRequest {
        val request = SimulerBeregningRequest()
        request.request =
            no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest()
        request.request.oppdrag = Oppdrag()
        request.request.oppdrag.kodeEndring = "ENDR"
        request.request.oppdrag.kodeFagomraade = "BA"
        request.request.oppdrag.fagsystemId = "223456789"
        request.request.oppdrag.oppdragGjelderId = "12345678902"
        request.request.oppdrag.saksbehId = "saksbeh"
        val oppdragslinje = Oppdragslinje()
        oppdragslinje.kodeEndringLinje = "ENDR"
        oppdragslinje.kodeStatusLinje = KodeStatusLinje.OPPH
        oppdragslinje.vedtakId = "2020-11-27"
        oppdragslinje.delytelseId = "1122334455667700"
        oppdragslinje.kodeKlassifik = "FPADATORD"
        oppdragslinje.datoVedtakFom = "2020-07-27"
        oppdragslinje.datoVedtakTom = "2020-11-08"
        oppdragslinje.datoStatusFom = "2020-07-27"
        oppdragslinje.sats = BigDecimal.valueOf(1330)
        oppdragslinje.typeSats = "MND"
        oppdragslinje.saksbehId = "saksbeh"
        oppdragslinje.utbetalesTilId = "12345678902"
        oppdragslinje.henvisning = "123456"
        request.request.oppdrag.oppdragslinje.add(oppdragslinje)
        val oppdragslinje2 = Oppdragslinje()
        oppdragslinje2.kodeEndringLinje = "NY"
        oppdragslinje2.vedtakId = "2020-11-27"
        oppdragslinje2.delytelseId = "1122334455667700"
        oppdragslinje2.kodeKlassifik = "FPADATORD"
        oppdragslinje2.datoVedtakFom = "2020-07-27"
        oppdragslinje2.datoVedtakTom = "2020-11-08"
        oppdragslinje2.sats = BigDecimal.valueOf(1200)
        oppdragslinje2.typeSats = "MND"
        oppdragslinje2.saksbehId = "saksbeh"
        oppdragslinje2.utbetalesTilId = "12345678902"
        oppdragslinje2.henvisning = "123456"
        request.request.oppdrag.oppdragslinje.add(oppdragslinje2)
        return request
    }
    private fun simTestDataBaOpphør(): SimulerBeregningRequest {
        val request = SimulerBeregningRequest()
        request.request =
            no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest()
        request.request.oppdrag = Oppdrag()
        request.request.oppdrag.kodeEndring = "ENDR"
        request.request.oppdrag.kodeFagomraade = "BA"
        request.request.oppdrag.fagsystemId = "323456789"
        request.request.oppdrag.oppdragGjelderId = "12345678903"
        request.request.oppdrag.saksbehId = "saksbeh"
        val oppdragslinje = Oppdragslinje()
        oppdragslinje.kodeEndringLinje = "ENDR"
        oppdragslinje.kodeStatusLinje = KodeStatusLinje.OPPH
        oppdragslinje.vedtakId = "2020-11-27"
        oppdragslinje.delytelseId = "1122334455667700"
        oppdragslinje.kodeKlassifik = "FPADATORD"
        oppdragslinje.datoVedtakFom = "2020-07-27"
        oppdragslinje.datoVedtakTom = "2020-11-08"
        oppdragslinje.datoStatusFom = "2020-07-27"
        oppdragslinje.sats = BigDecimal.valueOf(1330)
        oppdragslinje.typeSats = "MND"
        oppdragslinje.saksbehId = "saksbeh"
        oppdragslinje.utbetalesTilId = "12345678903"
        oppdragslinje.henvisning = "123456"
        request.request.oppdrag.oppdragslinje.add(oppdragslinje)
        return request
    }
}
