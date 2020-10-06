package no.nav.familie.oppdrag.avstemming

import no.nav.virksomhet.tjenester.avstemming.informasjon.konsistensavstemmingsdata.v1.Konsistensavstemmingsdata
import no.nav.virksomhet.tjenester.avstemming.meldinger.v1.Avstemmingsdata
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("e2e")
class AvstemmingSenderMock() : AvstemmingSender {

    override fun sendGrensesnittAvstemming(avstemmingsdata: Avstemmingsdata) {
    }

    override fun sendKonsistensAvstemming(avstemmingsdata: Konsistensavstemmingsdata) {
    }
}