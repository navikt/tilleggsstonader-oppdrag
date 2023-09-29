package no.nav.tilleggsstonader.oppdrag.repository

import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag

data class UtbetalingsoppdragForKonsistensavstemming(
    val fagsakId: String,
    val behandlingId: String,
    val utbetalingsoppdrag: Utbetalingsoppdrag,
)
