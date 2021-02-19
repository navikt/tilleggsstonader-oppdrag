package no.nav.familie.oppdrag.simulering.util

import io.mockk.every
import io.mockk.mockk
import no.nav.familie.oppdrag.avstemming.AvstemmingSender
import no.nav.familie.oppdrag.service.OppdragService
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
class ClientMocks {

    @Bean
    @Profile("dev", "integrasjonstest")
    @Primary
    fun mockSimulerFpService(): SimulerFpService {
        val simulerFpService = mockk<SimulerFpService>()

        every {
            simulerFpService.simulerBeregning(any())
        } answers {
            lagTestSimuleringResponse()
        }

        return simulerFpService
    }

    @Bean
    @Profile("integrasjonstest")
    @Primary
    fun avstemmingSenderMQ() = mockk<AvstemmingSender>()

    @Bean
    @Profile("integrasjonstest")
    @Primary
    fun oppdragServiceImpl() = mockk<OppdragService>()

}
