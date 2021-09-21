package no.nav.familie.oppdrag.config

enum class Integrasjonssystem {
    TILBAKEKREVING,
    SIMULERING
}

open class IntegrasjonException(val system: Integrasjonssystem,
                                msg: String,
                                throwable: Throwable? = null) : RuntimeException(msg, throwable)
