package no.nav.familie.oppdrag.rest

import no.nav.familie.kontrakter.felles.oppdrag.Opphør
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsperiode
import no.nav.familie.oppdrag.domene.id
import no.nav.familie.oppdrag.iverksetting.OppdragMapper
import no.nav.familie.oppdrag.repository.OppdragProtokollRepository
import no.nav.familie.oppdrag.repository.OppdragProtokollStatus
import no.nav.familie.oppdrag.service.OppdragService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DuplicateKeyException
import org.springframework.jms.annotation.EnableJms
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Configuration
@ComponentScan("no.nav.familie.oppdrag") class TestConfig

@ActiveProfiles("dev")
@SpringBootTest(classes = [TestConfig::class], properties = ["spring.cloud.vault.enabled=false"])
@EnableJms
@Disabled
internal class OppdragControllerIntegrasjonTest {

    final val localDateTimeNow = LocalDateTime.now()
    final val localDateNow = LocalDate.now()

    val utbetalingsoppdragMedTilfeldigAktoer = Utbetalingsoppdrag(
            Utbetalingsoppdrag.KodeEndring.NY,
            "TEST",
            "SAKSNR",
            UUID.randomUUID().toString(), // Foreløpig plass til en 50-tegn string og ingen gyldighetssjekk
            "SAKSBEHANDLERID",
            localDateTimeNow,
            listOf(Utbetalingsperiode(false,
                                      Opphør(localDateNow),
                                      localDateNow,
                                      "KLASSE A",
                                      localDateNow,
                                      localDateNow,
                                      BigDecimal.ONE,
                                      Utbetalingsperiode.SatsType.MND,
                                      "UTEBETALES_TIL",
                                      1))
    )

    @Autowired lateinit var oppdragService: OppdragService
    @Autowired lateinit var oppdragProtokollRepository: OppdragProtokollRepository

    @Test
    fun test_skal_lagre_oppdragprotokoll_for_utbetalingoppdrag() {

        val mapper = OppdragMapper()

        val oppdragController = OppdragController(oppdragService, mapper)

        oppdragController.sendOppdrag(utbetalingsoppdragMedTilfeldigAktoer)

        var oppdragStatus: OppdragProtokollStatus;

        do {
            val oppdrag = oppdragProtokollRepository.hentOppdrag(utbetalingsoppdragMedTilfeldigAktoer.id)
            oppdragStatus = oppdrag.status

        } while (oppdragStatus == OppdragProtokollStatus.LAGT_PÅ_KØ)

        assertEquals( OppdragProtokollStatus.KVITTERT_UKJENT,oppdragStatus)
    }
}