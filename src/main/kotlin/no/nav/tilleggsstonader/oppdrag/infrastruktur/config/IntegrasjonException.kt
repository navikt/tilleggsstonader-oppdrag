package no.nav.tilleggsstonader.oppdrag.infrastruktur.config

enum class Integrasjonssystem {
    TILBAKEKREVING,
    SIMULERING,
}

open class IntegrasjonException(
    val system: Integrasjonssystem,
    msg: String,
    throwable: Throwable? = null,
) : RuntimeException(msg, throwable)

open class FinnesIkkeITps(val system: Integrasjonssystem) : RuntimeException()
