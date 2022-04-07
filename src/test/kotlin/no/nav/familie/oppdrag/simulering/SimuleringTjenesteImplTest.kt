package no.nav.familie.oppdrag.simulering

import no.nav.familie.kontrakter.felles.simulering.HentFeilutbetalingerFraSimuleringRequest
import no.nav.familie.kontrakter.felles.tilbakekreving.Ytelsestype
import no.nav.familie.oppdrag.repository.SimuleringLager
import no.nav.familie.oppdrag.repository.SimuleringLagerTjeneste
import no.nav.familie.oppdrag.simulering.util.lagTestUtbetalingsoppdragForFGBMedEttBarn
import no.nav.familie.oppdrag.util.Containers
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ActiveProfiles("dev")
@ContextConfiguration(initializers = arrayOf(Containers.PostgresSQLInitializer::class))
@SpringBootTest(classes = [SimuleringTjenesteImplTest.TestConfig::class], properties = ["spring.cloud.vault.enabled=false"])
@DisabledIfEnvironmentVariable(named = "CIRCLECI", matches = "true")
@Testcontainers
internal class SimuleringTjenesteImplTest {

    @Autowired lateinit var simuleringLagerTjeneste: SimuleringLagerTjeneste
    @Autowired lateinit var simuleringTjeneste: SimuleringTjeneste

    companion object {

        @Container var postgreSQLContainer = Containers.postgreSQLContainer
    }

    @Test
    fun skal_lagre_request_og_respons() {
        val utbetalingsoppdrag = lagTestUtbetalingsoppdragForFGBMedEttBarn()

        val simuleringResultat = simuleringTjeneste.utførSimuleringOghentDetaljertSimuleringResultat(utbetalingsoppdrag)

        assertNotNull(simuleringResultat)

        val alleLagretSimuleringsLager = simuleringLagerTjeneste.finnAlleSimuleringsLager()
        assertEquals(1, alleLagretSimuleringsLager.size)
        val simuleringsLager = alleLagretSimuleringsLager[0]
        assertNotNull(simuleringsLager.requestXml)
        assertNotNull(simuleringsLager.responseXml)
    }

    @Test
    fun `hentFeilutbetalinger skal hente feilutbetalinger`() {
        val eksternFagsakId = "10001"
        val fagsystemsbehandlingId = "2054"
        val utbetalingsoppdrag = lesFil("/simulering/testdata/utbetalingsoppdrag_fagsak_10001_EFOG.txt")
        val requestXml = lesFil("/simulering/testdata/requestXML_fagsak_10001_EFOG.xml")
        val responsXml = lesFil("/simulering/testdata/responsXML_fagsak_10001_EFOG.xml")

        simuleringLagerTjeneste.lagreINyTransaksjon(SimuleringLager(
                fagsystem = "EFOG",
                fagsakId = eksternFagsakId,
                behandlingId = fagsystemsbehandlingId,
                utbetalingsoppdrag = utbetalingsoppdrag,
                requestXml = requestXml,
                responseXml = responsXml
        ))

        val feilutbetalingerFraSimulering = simuleringTjeneste
                .hentFeilutbetalinger(HentFeilutbetalingerFraSimuleringRequest(ytelsestype = Ytelsestype.OVERGANGSSTØNAD,
                                                                               eksternFagsakId = eksternFagsakId,
                                                                               fagsystemsbehandlingId = fagsystemsbehandlingId))
        assertTrue {
            feilutbetalingerFraSimulering.feilutbetaltePerioder.isNotEmpty() &&
            feilutbetalingerFraSimulering.feilutbetaltePerioder.size == 1
        }

        val feilutbetaltPeriode = feilutbetalingerFraSimulering.feilutbetaltePerioder[0]
        assertEquals(LocalDate.of(2022, 3, 1), feilutbetaltPeriode.fom)
        assertEquals(LocalDate.of(2022, 3, 31), feilutbetaltPeriode.tom)
        assertEquals(BigDecimal("10120.00"), feilutbetaltPeriode.feilutbetaltBeløp)
        assertEquals(BigDecimal("12570.00"), feilutbetaltPeriode.tidligereUtbetaltBeløp)
        assertEquals(BigDecimal("2450.00"), feilutbetaltPeriode.nyttBeløp)
    }

    private fun lesFil(fileName: String): String {
        val url = requireNotNull(this::class.java.getResource(fileName)) { "fil med filnavn=$fileName finnes ikke" }
        return url.readText()
    }


    @Configuration
    @ComponentScan(basePackages = ["no.nav.familie.oppdrag"],
                   excludeFilters = [ComponentScan.Filter(type = FilterType.REGEX, pattern = [".*[MQ].*"])])
    class TestConfig
}
