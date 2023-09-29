package no.nav.tilleggsstonader.oppdrag.service

import no.nav.familie.kontrakter.felles.oppdrag.OppdragId
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.tilleggsstonader.oppdrag.repository.OppdragLager
import no.trygdeetaten.skjema.oppdrag.Oppdrag

interface OppdragService {
    fun opprettOppdrag(utbetalingsoppdrag: Utbetalingsoppdrag, oppdrag: Oppdrag, versjon: Int)
    fun hentStatusForOppdrag(oppdragId: OppdragId): OppdragLager
    fun resendOppdrag(oppdragId: OppdragId)
}
