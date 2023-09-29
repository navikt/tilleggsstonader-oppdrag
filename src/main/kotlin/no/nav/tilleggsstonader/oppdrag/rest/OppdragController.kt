package no.nav.tilleggsstonader.oppdrag.rest

import jakarta.validation.Valid
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.kontrakter.felles.oppdrag.OppdragId
import no.nav.familie.kontrakter.felles.oppdrag.OppdragStatus
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilleggsstonader.oppdrag.common.RessursUtils.conflict
import no.nav.tilleggsstonader.oppdrag.common.RessursUtils.illegalState
import no.nav.tilleggsstonader.oppdrag.common.RessursUtils.notFound
import no.nav.tilleggsstonader.oppdrag.common.RessursUtils.ok
import no.nav.tilleggsstonader.oppdrag.iverksetting.OppdragMapper
import no.nav.tilleggsstonader.oppdrag.service.OppdragAlleredeSendtException
import no.nav.tilleggsstonader.oppdrag.service.OppdragService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ProtectedWithClaims(issuer = "azuread")
class OppdragController(
    @Autowired val oppdragService: OppdragService,
    @Autowired val oppdragMapper: OppdragMapper,
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], path = ["/oppdrag"])
    fun sendOppdrag(
        @Valid @RequestBody
        utbetalingsoppdrag: Utbetalingsoppdrag,
    ): ResponseEntity<Ressurs<String>> {
        return Result.runCatching {
            val oppdrag110 = oppdragMapper.tilOppdrag110(utbetalingsoppdrag)
            val oppdrag = oppdragMapper.tilOppdrag(oppdrag110)

            oppdragService.opprettOppdrag(utbetalingsoppdrag, oppdrag, 0)
        }.fold(
            onFailure = {
                if (it is OppdragAlleredeSendtException) {
                    conflict("Oppdrag er allerede sendt for saksnr ${utbetalingsoppdrag.saksnummer}.")
                } else {
                    illegalState("Klarte ikke sende oppdrag for saksnr ${utbetalingsoppdrag.saksnummer}", it)
                }
            },
            onSuccess = {
                ok("Oppdrag sendt OK")
            },
        )
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], path = ["/oppdragPaaNytt/{versjon}"])
    fun sendOppdragPåNytt(
        @Valid @RequestBody
        utbetalingsoppdrag: Utbetalingsoppdrag,
        @PathVariable versjon: Int,
    ): ResponseEntity<Ressurs<String>> {
        return Result.runCatching {
            val oppdrag110 = oppdragMapper.tilOppdrag110(utbetalingsoppdrag)
            val oppdrag = oppdragMapper.tilOppdrag(oppdrag110)

            oppdragService.opprettOppdrag(utbetalingsoppdrag, oppdrag, versjon)
        }.fold(
            onFailure = {
                illegalState("Klarte ikke sende oppdrag for saksnr ${utbetalingsoppdrag.saksnummer}", it)
            },
            onSuccess = {
                ok("Oppdrag sendt OK")
            },
        )
    }

    @PostMapping("resend")
    fun resentOppdrag(
        @Valid @RequestBody
        oppdragId: OppdragId,
    ) {
        oppdragService.resendOppdrag(oppdragId)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], path = ["/status"])
    fun hentStatus(
        @Valid @RequestBody
        oppdragId: OppdragId,
    ): ResponseEntity<Ressurs<OppdragStatus>> {
        return Result.runCatching { oppdragService.hentStatusForOppdrag(oppdragId) }
            .fold(
                onFailure = {
                    notFound("Fant ikke oppdrag med id $oppdragId")
                },
                onSuccess = {
                    ok(it.status, it.kvitteringsmelding?.beskrMelding ?: "Savner kvitteringsmelding")
                },
            )
    }
}
