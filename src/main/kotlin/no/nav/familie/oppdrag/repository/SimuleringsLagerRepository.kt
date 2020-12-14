package no.nav.familie.oppdrag.repository

interface SimuleringsLagerRepository {

    fun opprettSimulering(simuleringsLager: SimuleringsLager, versjon: Int = 0)
}
