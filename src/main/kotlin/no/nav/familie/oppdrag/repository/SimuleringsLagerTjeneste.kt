package no.nav.familie.oppdrag.repository

interface SimuleringsLagerTjeneste {

    fun lagreINyTransaksjon(simuleringsLager: SimuleringsLager)
    fun oppdater(simuleringsLager: SimuleringsLager)
    fun finnAlleSimuleringsLager(): List<SimuleringsLager>
}
