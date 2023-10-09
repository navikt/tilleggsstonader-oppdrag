package no.nav.tilleggsstonader.oppdrag.simulering.mock

import io.mockk.every
import io.mockk.mockk
import no.nav.tilleggsstonader.oppdrag.simulering.SimuleringSender
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local", "local_psql_mq")
@Configuration
class SimuleringConfig {

    @Bean
    fun simuleringSender(): SimuleringSender {
        val service = mockk<SimuleringSender>()
        every { service.hentSimulerBeregningResponse(any()) } answers {
            SimuleringGenerator().opprettSimuleringsResultat(firstArg())
        }
        return service
    }

}
