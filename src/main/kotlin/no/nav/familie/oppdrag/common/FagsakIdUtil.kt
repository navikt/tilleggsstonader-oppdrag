package no.nav.familie.oppdrag.common

import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag

fun Utbetalingsoppdrag.fagsystemId(springProfile: String = getSpringProfile()): String {
    return if (springProfile.contains("preprod")) {
        "0" + this.saksnummer
    } else {
        this.saksnummer
    }
}

private fun getSpringProfile() = System.getenv("SPRING_PROFILES_ACTIVE") ?: ""
