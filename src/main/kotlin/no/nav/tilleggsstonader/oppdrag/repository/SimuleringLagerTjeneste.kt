package no.nav.tilleggsstonader.oppdrag.repository

interface SimuleringLagerTjeneste {

    fun lagreINyTransaksjon(simuleringLager: SimuleringLager)
    fun oppdater(simuleringLager: SimuleringLager)
    fun finnAlleSimuleringsLager(): List<SimuleringLager>
    fun hentSisteSimuleringsresultat(fagsystem: String, fagsakId: String, behandlingId: String): SimuleringLager
}
