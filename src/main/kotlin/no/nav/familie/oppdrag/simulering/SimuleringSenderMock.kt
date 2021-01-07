package no.nav.familie.oppdrag.simulering

import no.nav.familie.oppdrag.simulering.mock.SimuleringGenerator
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("dev")
@Service
class SimuleringSenderMock : SimuleringSender {

    override fun hentSimulerBeregningResponse(simulerBeregningRequest: SimulerBeregningRequest?): SimulerBeregningResponse {
        return SimuleringGenerator().opprettSimuleringsResultat(simulerBeregningRequest!!)
    }

}
