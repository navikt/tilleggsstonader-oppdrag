package no.nav.tilleggsstonader.oppdrag

import no.nav.tilleggsstonader.oppdrag.infrastruktur.config.ApplicationConfig
import org.springframework.boot.builder.SpringApplicationBuilder

object LocalApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val app = SpringApplicationBuilder(ApplicationConfig::class.java)
            .profiles("local")
        app.run(*args)
    }
}
