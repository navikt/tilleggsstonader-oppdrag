package no.nav.tilleggsstonader.oppdrag.config

import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.tilleggsstonader.oppdrag.common.RessursUtils.illegalState
import no.nav.tilleggsstonader.oppdrag.common.RessursUtils.serviceUnavailable
import no.nav.tilleggsstonader.oppdrag.common.RessursUtils.unauthorized
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.NestedExceptionUtils.getMostSpecificCause
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ApiExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val secureLogger = LoggerFactory.getLogger("secureLogger")

    @ExceptionHandler(JwtTokenUnauthorizedException::class)
    fun handleJwtTokenUnauthorizedException(jwtTokenUnauthorizedException: JwtTokenUnauthorizedException): ResponseEntity<Ressurs<Nothing>> {
        return unauthorized("Unauthorized")
    }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(throwable: Throwable): ResponseEntity<Ressurs<Nothing>> {
        val mostSpecificThrowable = getMostSpecificCause(throwable)
        return illegalState(mostSpecificThrowable.message.toString(), mostSpecificThrowable)
    }

    @ExceptionHandler(IntegrasjonException::class)
    fun handleThrowable(feil: IntegrasjonException): ResponseEntity<Ressurs<Nothing>> {
        secureLogger.error("Feil mot ${feil.system} har oppstått", feil)
        logger.error("Feil mot ${feil.system} har oppstått exception=${getMostSpecificCause(feil)::class}")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Ressurs.failure(errorMessage = feil.message))
    }

    @ExceptionHandler(FinnesIkkeITps::class)
    fun handleThrowable(feil: FinnesIkkeITps): ResponseEntity<Ressurs<Nothing>> {
        logger.warn("Personen finnes ikke i TPS system=${feil.system} ")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Ressurs.failure(errorMessage = "Personen finnes ikke i TPS"))
    }
}
