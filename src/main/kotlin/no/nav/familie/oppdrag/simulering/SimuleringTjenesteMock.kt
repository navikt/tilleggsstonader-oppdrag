package no.nav.familie.oppdrag.simulering

import no.nav.familie.kontrakter.felles.oppdrag.RestSimulerResultat
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.oppdrag.simulering.mock.SimuleringGenerator
import no.nav.familie.oppdrag.simulering.repository.DetaljertSimuleringResultat
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.ApplicationScope

@Service
@ApplicationScope
@Profile("dev")
@Primary
class SimuleringTjenesteMock(@Autowired val simulerBeregningRequestMapper: SimulerBeregningRequestMapper): SimuleringTjeneste {

    override fun utførSimulering(utbetalingsoppdrag: Utbetalingsoppdrag): RestSimulerResultat {
        return mockSimuleringRespons(utbetalingsoppdrag).toRestSimulerResult()
    }
    override fun utførSimuleringOghentDetaljertSimuleringResultat(utbetalingsoppdrag: Utbetalingsoppdrag): DetaljertSimuleringResultat {
        val beregning = mockSimuleringRespons(utbetalingsoppdrag).response.simulering
        return SimuleringResultatTransformer().mapSimulering(beregning = beregning, utbetalingsoppdrag = utbetalingsoppdrag)
    }
    override fun hentSimulerBeregningResponse(utbetalingsoppdrag: Utbetalingsoppdrag): SimulerBeregningResponse {
        return mockSimuleringRespons(utbetalingsoppdrag)
    }

    private fun mockSimuleringRespons(utbetalingsoppdrag: Utbetalingsoppdrag): SimulerBeregningResponse{
        val simulerBeregningRequest = simulerBeregningRequestMapper.tilSimulerBeregningRequest(utbetalingsoppdrag)
        val respons = SimuleringGenerator().opprettSimuleringsResultat(simulerBeregningRequest)
        return respons
    }
}
