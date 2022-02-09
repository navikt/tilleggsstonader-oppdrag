package no.nav.familie.oppdrag.repository

import no.nav.familie.oppdrag.service.Fagsystem
import no.nav.familie.oppdrag.util.Containers
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
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ActiveProfiles("dev")
@ContextConfiguration(initializers = arrayOf(Containers.PostgresSQLInitializer::class))
@SpringBootTest(classes = [MellomlagringKonsistensavstemmingRepositoryTest.TestConfig::class], properties = ["spring.cloud.vault.enabled=false"])
@DisabledIfEnvironmentVariable(named = "CIRCLECI", matches = "true")
@Testcontainers
internal class MellomlagringKonsistensavstemmingRepositoryTest {

    @Autowired lateinit var repository: MellomlagringKonsistensavstemmingRepository
    private val avstemingstidspunkt = LocalDateTime.now().format(MellomlagringKonsistensavstemming.avstemingstidspunktFormater)

    companion object {

        @Container var postgreSQLContainer = Containers.postgreSQLContainer
    }

    @Test
    fun `Test lesing aggregert beløp og antall oppgaver fra tom tabell`() {
        assertEquals(0, repository.hentAggregertTotalBeløp(Fagsystem.BA, avstemingstidspunkt))
        assertEquals(0, repository.hentAggregertAntallOppdrag(Fagsystem.BA, avstemingstidspunkt))
    }

    @Test
    fun `Test lesing aggregert beløp og antall oppgaver`() {
        repository.insert(opprettMellomlagringKonsistensavstemming(10, 161))
        repository.insert(opprettMellomlagringKonsistensavstemming(30, 222))

        assertEquals(383, repository.hentAggregertTotalBeløp(Fagsystem.BA, avstemingstidspunkt))
        assertEquals(40, repository.hentAggregertAntallOppdrag(Fagsystem.BA, avstemingstidspunkt))
    }

    @Test
    fun `Test nullstilling`() {

        repository.insert(opprettMellomlagringKonsistensavstemming(10, 161))
        repository.insert(opprettMellomlagringKonsistensavstemming(30, 222))
        nullstillMellomlagring(Fagsystem.BA, avstemingstidspunkt)

        assertEquals(0, repository.hentAggregertTotalBeløp(Fagsystem.BA, avstemingstidspunkt))
        assertEquals(0, repository.hentAggregertAntallOppdrag(Fagsystem.BA, avstemingstidspunkt))
    }

    fun nullstillMellomlagring(fagsystem: Fagsystem, avstemingstidspunkt: String) {
        val deaktivertMellomlagring =
            repository.findAllByFagsystemAndAvstemmingstidspunktAndAktiv(
                fagsystem,
                avstemingstidspunkt,
                true
            ).map { mk -> mk.also { it.aktiv = false } }

        repository.updateAll(deaktivertMellomlagring)
    }

    fun opprettMellomlagringKonsistensavstemming(antallOppdrag: Int, totalBeløp: Long) = MellomlagringKonsistensavstemming(
        fagsystem = Fagsystem.BA,
        avstemmingstidspunkt = avstemingstidspunkt.format(MellomlagringKonsistensavstemming.avstemingstidspunktFormater),
        antallOppdrag = antallOppdrag,
        totalBeløp = totalBeløp
    )
    @Configuration
    @ComponentScan(basePackages = ["no.nav.familie.oppdrag"],
                   excludeFilters = [ComponentScan.Filter(type = FilterType.REGEX, pattern = [".*[MQ].*"])])
    class TestConfig
}


