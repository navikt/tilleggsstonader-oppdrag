package no.nav.familie.oppdrag.service

import io.mockk.every
import io.mockk.mockk
import no.nav.familie.oppdrag.repository.MellomlagringKonsistensavstemmingRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class MellomlagringKonsistensavstemmingServiceTest {

    private lateinit var mellomlagringKonsistensavstemmingRepository: MellomlagringKonsistensavstemmingRepository
    private lateinit var mellomlagringKonsistensavstemmingService: MellomlagringKonsistensavstemmingService

    private val avstemmingstidspunkt = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        mellomlagringKonsistensavstemmingRepository = mockk()
        mellomlagringKonsistensavstemmingService =
            MellomlagringKonsistensavstemmingService(mellomlagringKonsistensavstemmingRepository = mellomlagringKonsistensavstemmingRepository)
    }

    @Test
    fun `Hent aggregert beløp hvor ikke splittet batch`() {
        assertEquals(0, mellomlagringKonsistensavstemmingService.hentAggregertBeløp(opprettMetaInfo(true, true)))
    }

    @Test
    fun `Hent aggregert beløp for siste batch i splittet batch`() {
        val metaInfo = opprettMetaInfo(false, true)

        every {
            mellomlagringKonsistensavstemmingRepository.hentaggregertTotalBeløp(
                metaInfo.fagsystem,
                metaInfo.avstemmingstidspunkt
            )
        } returns 100L

        assertEquals(100, mellomlagringKonsistensavstemmingService.hentAggregertBeløp(metaInfo))
    }

    @Test
    fun `Hent aggregert antall oppdrag hvor ikke splittet batch`() {
        assertEquals(0, mellomlagringKonsistensavstemmingService.hentAggregertAntallOppdrag(opprettMetaInfo(true, true)))
    }

    @Test
    fun `Hent aggregert antall oppdrag for siste batch i splittet batch`() {
        val metaInfo = opprettMetaInfo(false, true)

        every {
            mellomlagringKonsistensavstemmingRepository.hentAggregertAntallOppdrag(
                metaInfo.fagsystem,
                metaInfo.avstemmingstidspunkt
            )
        } returns 100

        assertEquals(100, mellomlagringKonsistensavstemmingService.hentAggregertAntallOppdrag(metaInfo))
    }

    private fun opprettMetaInfo(
        sendStartmelding: Boolean,
        sendAvsluttmelding: Boolean,
    ) =
        KonsistensavstemmingMetaInfo(Fagsystem.BA, avstemmingstidspunkt, sendStartmelding, sendAvsluttmelding, emptyList())
}
