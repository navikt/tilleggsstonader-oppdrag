package no.nav.tilleggsstonader.oppdrag.iverksetting

import no.trygdeetaten.skjema.oppdrag.Oppdrag

interface OppdragSender {
    fun sendOppdrag(oppdrag: Oppdrag): String
}
