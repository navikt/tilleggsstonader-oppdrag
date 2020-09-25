package no.nav.familie.oppdrag.rest

import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.oppdrag.simulering.SimulerResultatDto
import no.nav.familie.oppdrag.simulering.SimuleringTjeneste
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/simulering")
@ProtectedWithClaims(issuer = "azuread")
class SimuleringController(@Autowired val simuleringTjeneste: SimuleringTjeneste) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], path = ["/etterbetalingsbelop"])
    fun startSimulering(@Valid @RequestBody utbetalingsoppdrag: Utbetalingsoppdrag): ResponseEntity<Ressurs<SimulerResultatDto>> {
        LOG.info("Hente simulert etterbetaling for saksnr ${utbetalingsoppdrag.saksnummer}")

        return Result.runCatching {
            simuleringTjeneste.utførSimulering(utbetalingsoppdrag)
        }.fold(
                onSuccess = {
                    ResponseEntity.ok(Ressurs.success(it))
                },
                onFailure = {
                    secureLog.error("Feil ved simulering av etterbetaling:", it)
                    ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Ressurs.failure(errorMessage = "Klarte ikke hente simulert etterbetaling for saksnr ${utbetalingsoppdrag.saksnummer}"))
                }
        )
    }

    companion object {

        val LOG = LoggerFactory.getLogger(SimuleringController::class.java)
        val secureLog = LoggerFactory.getLogger("secureLogger")
    }
}