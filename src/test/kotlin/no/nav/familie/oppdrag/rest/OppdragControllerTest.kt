package no.nav.familie.oppdrag.rest

import io.mockk.*
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.kontrakter.felles.oppdrag.OppdragStatus
import no.nav.familie.kontrakter.felles.oppdrag.Opphør
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsperiode
import no.nav.familie.oppdrag.iverksetting.OppdragMapper
import no.nav.familie.oppdrag.iverksetting.OppdragSender
import no.nav.familie.oppdrag.repository.OppdragLager
import no.nav.familie.oppdrag.repository.OppdragLagerRepository
import no.nav.familie.oppdrag.service.OppdragServiceImpl
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal class OppdragControllerTest {

    val localDateTimeNow = LocalDateTime.now()
    val localDateNow = LocalDate.now()
    val oppdragLager = OppdragLager(
        fagsystem = "BA",
        personIdent = "12345678910",
        fagsakId = "1234",
        behandlingId = "1234",
        utbetalingsoppdrag = Utbetalingsoppdrag(
            aktoer = "1234567891012",
            fagSystem = "BA",
            kodeEndring = Utbetalingsoppdrag.KodeEndring.NY,
            saksbehandlerId = "Z123", saksnummer = "9999991",
            utbetalingsperiode = emptyList()
        ),
        utgåendeOppdrag = "999992",
        status = OppdragStatus.KVITTERT_OK,
        avstemmingTidspunkt = LocalDateTime.now(),
        kvitteringsmelding = null,
    )

    val utbetalingsoppdrag = Utbetalingsoppdrag(
        Utbetalingsoppdrag.KodeEndring.NY,
        "BA",
        "SAKSNR",
        "PERSONID",
        "SAKSBEHANDLERID",
        localDateTimeNow,
        listOf(
            Utbetalingsperiode(
                true,
                Opphør(localDateNow),
                2,
                1,
                localDateNow,
                "BATR",
                localDateNow,
                localDateNow,
                BigDecimal.ONE,
                Utbetalingsperiode.SatsType.MND,
                "UTEBETALES_TIL",
                1
            )
        )
    )

    @Test
    fun `Skal lagre oppdrag for utbetalingoppdrag`() {

        val (oppdragLagerRepository, oppdragController) = mockkOppdragController(null)

        oppdragController.sendOppdrag(utbetalingsoppdrag)

        verify {
            oppdragLagerRepository.opprettOppdrag(
                match<OppdragLager> {
                    it.utgåendeOppdrag.contains("BA") &&
                        it.status == OppdragStatus.LAGT_PÅ_KØ &&
                        it.opprettetTidspunkt > localDateTimeNow
                }
            )
        }
    }

    @Test
    fun `Skal kaste feil om oppdrag er lagret fra før`() {
        val (oppdragLagerRepository, oppdragController) = mockkOppdragController(oppdragLager)

        val response = oppdragController.sendOppdrag(utbetalingsoppdrag)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(Ressurs.Status.FEILET, response.body?.status)

        verify(exactly = 1) { oppdragLagerRepository.finnOppdrag(any(), 0) }
        verify(exactly = 0) { oppdragLagerRepository.opprettOppdrag(any()) }
    }

    private fun mockkOppdragController(persistertOppdragLager: OppdragLager?): Pair<OppdragLagerRepository, OppdragController> {
        val mapper = OppdragMapper()
        val oppdragSender = mockk<OppdragSender>(relaxed = true)

        val oppdragLagerRepository = mockk<OppdragLagerRepository>()
        every { oppdragLagerRepository.opprettOppdrag(any()) } just Runs
        every { oppdragLagerRepository.finnOppdrag(any(), 0) } returns persistertOppdragLager

        val oppdragService = OppdragServiceImpl(oppdragSender, oppdragLagerRepository)

        val oppdragController = OppdragController(oppdragService, mapper)
        return Pair(oppdragLagerRepository, oppdragController)
    }
}
