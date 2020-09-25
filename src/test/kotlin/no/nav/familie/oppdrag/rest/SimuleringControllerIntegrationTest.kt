package no.nav.familie.oppdrag.rest

import no.nav.familie.oppdrag.simulering.SimulerBeregningRequestMapper
import no.nav.familie.oppdrag.simulering.SimulerBeregningResponseMapper
import no.nav.familie.oppdrag.simulering.SimuleringSender
import no.nav.familie.oppdrag.simulering.SimuleringTjeneste
import no.nav.familie.oppdrag.simulering.util.ClientMocks
import no.nav.familie.oppdrag.simulering.util.lagTestUtbetalingsoppdragForFGBMedEttBarn
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals


@ActiveProfiles("dev")
@SpringBootTest(classes = [SimuleringController::class, SimuleringSender::class, SimuleringTjeneste::class, SimulerBeregningRequestMapper::class, ClientMocks::class, SimulerBeregningResponseMapper::class],
                properties = ["spring.cloud.vault.enabled=false"])

internal class SimuleringControllerIntegrasjonTest {

    @Autowired lateinit var simuleringController: SimuleringController

    @Test
    fun test_etterbetalingsbelop() {
        val response = simuleringController.startSimulering(lagTestUtbetalingsoppdragForFGBMedEttBarn())
        assertEquals(1000L, response.body?.data?.etterbetaling)
    }
}