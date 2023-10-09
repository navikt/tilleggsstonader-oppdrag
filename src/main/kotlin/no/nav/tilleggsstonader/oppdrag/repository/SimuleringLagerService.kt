package no.nav.tilleggsstonader.oppdrag.repository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class SimuleringLagerService(
    private val simuleringLagerRepository: SimuleringLagerRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun lagreINyTransaksjon(simuleringLager: SimuleringLager) {
        simuleringLagerRepository.insert(simuleringLager)
    }

    fun oppdater(simuleringLager: SimuleringLager) {
        simuleringLagerRepository.update(simuleringLager)
    }

    fun finnAlleSimuleringsLager(): List<SimuleringLager> {
        return simuleringLagerRepository.findAll().toList()
    }

    fun hentSisteSimuleringsresultat(
        fagsystem: String,
        fagsakId: String,
        behandlingId: String
    ): SimuleringLager {
        return simuleringLagerRepository.finnSisteSimuleringsresultat(fagsystem, fagsakId, behandlingId)
    }
}
