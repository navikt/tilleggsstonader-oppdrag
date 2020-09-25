package no.nav.familie.oppdrag.simulering

import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.springframework.stereotype.Component

@Component
class SimulerBeregningResponseMapper {

    fun toSimulerResultDto(simulerBeregningResponse: SimulerBeregningResponse): SimulerResultatDto{
        // TODO: Mapp simuleringsresponse til etterbetalingsbelop.
        return SimulerResultatDto(etterbetaling = 1000)
    }
}