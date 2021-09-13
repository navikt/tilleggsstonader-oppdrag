package no.nav.familie.oppdrag.config

import java.net.URI

open class IntegrasjonException(msg: String,
                                throwable: Throwable? = null,
                                val uri: URI? = null,
                                val data: Any? = null) : RuntimeException(msg, throwable)
