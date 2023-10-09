package no.nav.tilleggsstonader.oppdrag

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.jms.annotation.EnableJms

@SpringBootApplication
@EnableJms
class App

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
}
