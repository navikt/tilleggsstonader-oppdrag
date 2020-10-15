package no.nav.familie.oppdrag.simulering

import no.nav.familie.kontrakter.felles.oppdrag.RestSimulerResultat
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.ApplicationScope

@Service
@ApplicationScope
class SimuleringTjeneste(@Autowired val simuleringSender: SimuleringSender,
                         @Autowired val simulerBeregningRequestMapper: SimulerBeregningRequestMapper,
                         @Autowired val simulerBeregningResponseMapper: SimulerBeregningResponseMapper) {

    fun utførSimulering(utbetalingsoppdrag: Utbetalingsoppdrag): RestSimulerResultat {
        return simulerBeregningResponseMapper.toRestSimulerResult(
                hentSimulerBeregningResponse(utbetalingsoppdrag)
        )
    }

    fun hentSimulerBeregningResponse(utbetalingsoppdrag: Utbetalingsoppdrag):SimulerBeregningResponse {
        val simulerBeregningRequest = simulerBeregningRequestMapper.tilSimulerBeregningRequest(utbetalingsoppdrag)
        return simuleringSender.hentSimulerBeregningResponse(simulerBeregningRequest)
    }
}