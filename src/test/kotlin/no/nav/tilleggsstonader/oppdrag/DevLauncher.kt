package no.nav.tilleggsstonader.oppdrag

import no.nav.tilleggsstonader.oppdrag.config.ApplicationConfig
import org.springframework.boot.builder.SpringApplicationBuilder

object DevLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val app = SpringApplicationBuilder(ApplicationConfig::class.java)
            .profiles("dev")
        app.run(*args)
    }
}
