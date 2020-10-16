package no.nav.familie.oppdrag.simulering

import no.nav.familie.kontrakter.felles.oppdrag.RestSimulerResultat
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerBeregningFeilUnderBehandling
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.ApplicationScope

@Service
@ApplicationScope
@Profile("!e2e")
class SimuleringTjenesteImpl(@Autowired val simuleringSender: SimuleringSender,
                             @Autowired val simulerBeregningRequestMapper: SimulerBeregningRequestMapper): SimuleringTjeneste {

    override fun utførSimulering(utbetalingsoppdrag: Utbetalingsoppdrag): RestSimulerResultat {
        return hentSimulerBeregningResponse(utbetalingsoppdrag).toRestSimulerResult()
    }

    override fun hentSimulerBeregningResponse(utbetalingsoppdrag: Utbetalingsoppdrag): SimulerBeregningResponse {
        val simulerBeregningRequest = simulerBeregningRequestMapper.tilSimulerBeregningRequest(utbetalingsoppdrag)

        return try {
            return simuleringSender.hentSimulerBeregningResponse(simulerBeregningRequest)
        } catch (ex: SimulerBeregningFeilUnderBehandling) {
            throw Exception(ex.message, ex)
        } catch (ex: Throwable) {
            throw Exception(ex.message, ex)
        }
    }
}
