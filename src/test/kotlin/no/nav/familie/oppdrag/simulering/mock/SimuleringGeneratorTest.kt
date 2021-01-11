package no.nav.familie.oppdrag.simulering.mock

import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaaDetaljer
import no.nav.system.os.entiteter.typer.simpletypes.KodeStatusLinje
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdrag
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdragslinje
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class SimuleringGeneratorTest {

    var simuleringGenerator = SimuleringGenerator()

    @Test
    fun SimuleringTestPositiv() {
        val oppdragGjelderId = "12345678901"
        val kodeEndring = "ENDR"
        val request: SimulerBeregningRequest = opprettSimulerBeregningRequest(oppdragGjelderId, kodeEndring)
        request.request.oppdrag.oppdragslinje.add(opprettOppdragslinje("NY", null, 2339, oppdragGjelderId))

        val response = simuleringGenerator.opprettSimuleringsResultat(request)
        assertThat(response.response.simulering.gjelderId).isEqualTo(oppdragGjelderId)
        assertThat(response.response.simulering.beregningsPeriode.size).isEqualTo(1)
        assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa.size).isEqualTo(5)
        var detaljer: List<BeregningStoppnivaaDetaljer> =
                response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer;
        detaljer.sortedBy { beregningStoppnivaaDetaljer -> beregningStoppnivaaDetaljer.behandlingskode }
        assertThat(detaljer.size).isEqualTo(1) //finnes ikke feilutbetaling
    }

    @Test
    fun SimuleringTestReduksjon() {
        val oppdragGjelderId = "12345678902"
        val kodeEndring = "ENDR"
        val request: SimulerBeregningRequest = opprettSimulerBeregningRequest(oppdragGjelderId, kodeEndring)
        request.request.oppdrag.oppdragslinje.add(opprettOppdragslinje(kodeEndring, KodeStatusLinje.OPPH, 1330, oppdragGjelderId))
        request.request.oppdrag.oppdragslinje.add(opprettOppdragslinje("NY", null, 1200, oppdragGjelderId))

        val response = simuleringGenerator.opprettSimuleringsResultat(request)
        assertThat(response.response.simulering.gjelderId).isEqualTo(oppdragGjelderId)
        assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa.size).isEqualTo(5)

        var detaljer: List<BeregningStoppnivaaDetaljer> =
                response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer
        assertThat(detaljer.size).isEqualTo(3) //finnes feilutbetaling, positiv og negatliv ytelse postering
        detaljer.sortedBy { beregningStoppnivaaDetaljer -> beregningStoppnivaaDetaljer.behandlingskode }
        assertThat(detaljer[0].typeKlasse).isEqualTo("YTEL")
        assertThat(detaljer[0].belop).isEqualTo(BigDecimal.valueOf(1200))
        assertThat(detaljer[1].typeKlasse).isEqualTo("FEIL")
        assertThat(detaljer[1].belop).isEqualTo(BigDecimal.valueOf(130))
        assertThat(detaljer[2].typeKlasse).isEqualTo("YTEL")
        assertThat(detaljer[2].belop).isEqualTo(BigDecimal.valueOf(-1330))
    }

    @Test
    fun SimuleringTestOpphør() {
        val oppdragGjelderId = "12345678903"
        val kodeEndring = "ENDR"
        val request: SimulerBeregningRequest = opprettSimulerBeregningRequest(oppdragGjelderId, kodeEndring)
        request.request.oppdrag.oppdragslinje.add(opprettOppdragslinje(kodeEndring, KodeStatusLinje.OPPH, 1330, oppdragGjelderId))

        val response = simuleringGenerator.opprettSimuleringsResultat(request)
        assertThat(response.response.simulering.gjelderId).isEqualTo(oppdragGjelderId)
        assertThat(response.response.simulering.beregningsPeriode[0].beregningStoppnivaa.size).isEqualTo(5)

        var detaljer: List<BeregningStoppnivaaDetaljer> =
                response.response.simulering.beregningsPeriode[0].beregningStoppnivaa[0].beregningStoppnivaaDetaljer
        assertThat(detaljer.size).isEqualTo(3) //finnes feilutbetaling, positiv og negatliv ytelse postering
        detaljer.sortedBy { beregningStoppnivaaDetaljer -> beregningStoppnivaaDetaljer.behandlingskode }
        assertThat(detaljer[0].typeKlasse).isEqualTo("YTEL")
        assertThat(detaljer[0].belop).isEqualTo(BigDecimal.valueOf(1330))
        assertThat(detaljer[1].typeKlasse).isEqualTo("FEIL")
        assertThat(detaljer[1].belop).isEqualTo(BigDecimal.valueOf(1330))
        assertThat(detaljer[2].typeKlasse).isEqualTo("YTEL")
        assertThat(detaljer[2].belop).isEqualTo(BigDecimal.valueOf(-1330))
    }

    private fun opprettSimulerBeregningRequest(oppdragGjelderId: String, kodeEndring: String): SimulerBeregningRequest {
        val request = SimulerBeregningRequest()
        request.request =
                no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest()
        request.request.oppdrag = Oppdrag()
        request.request.oppdrag.kodeEndring = kodeEndring
        request.request.oppdrag.kodeFagomraade = "BA"
        request.request.oppdrag.fagsystemId = "323456789"
        request.request.oppdrag.oppdragGjelderId = oppdragGjelderId
        request.request.oppdrag.saksbehId = "saksbeh"
        return request
    }

    private fun opprettOppdragslinje(kodeEndringLinje: String,
                                     kodeStatusLinje: KodeStatusLinje?,
                                     sats: Long,
                                     utbetalesTilId: String): Oppdragslinje {
        val oppdragslinje = Oppdragslinje()
        oppdragslinje.kodeEndringLinje = kodeEndringLinje
        oppdragslinje.kodeStatusLinje = kodeStatusLinje
        oppdragslinje.vedtakId = "2020-11-27"
        oppdragslinje.delytelseId = "1122334455667700"
        oppdragslinje.kodeKlassifik = "FPADATORD"
        oppdragslinje.datoVedtakFom = "2020-07-27"
        oppdragslinje.datoVedtakTom = "2020-11-08"
        oppdragslinje.datoStatusFom = "2020-07-27"
        oppdragslinje.sats = BigDecimal.valueOf(sats)
        oppdragslinje.typeSats = "MND"
        oppdragslinje.saksbehId = "saksbeh"
        oppdragslinje.utbetalesTilId = utbetalesTilId
        oppdragslinje.henvisning = "123456"
        return oppdragslinje
    }
}
