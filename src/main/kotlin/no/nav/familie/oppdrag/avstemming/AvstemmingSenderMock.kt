package no.nav.familie.oppdrag.avstemming

import no.nav.familie.oppdrag.grensesnittavstemming.JaxbGrensesnittAvstemmingsdata
import no.nav.familie.oppdrag.konsistensavstemming.JaxbKonsistensavstemming
import no.nav.virksomhet.tjenester.avstemming.informasjon.konsistensavstemmingsdata.v1.Konsistensavstemmingsdata
import no.nav.virksomhet.tjenester.avstemming.informasjon.konsistensavstemmingsdata.v1.SendAsynkronKonsistensavstemmingsdataRequest
import no.nav.virksomhet.tjenester.avstemming.meldinger.v1.Avstemmingsdata
import no.nav.virksomhet.tjenester.avstemming.v1.SendAsynkronKonsistensavstemmingsdata
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.jms.JmsException
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Service
@Profile("e2e")
class AvstemmingSenderMock() : AvstemmingSender {

    override fun sendGrensesnittAvstemming(avstemmingsdata: Avstemmingsdata) {
    }

    override fun sendKonsistensAvstemming(avstemmingsdata: Konsistensavstemmingsdata) {
    }
}