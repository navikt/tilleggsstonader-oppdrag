package no.nav.familie.oppdrag.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class SimuleringsLagerTjenesteImpl : SimuleringsLagerTjeneste {

    @Autowired lateinit var simuleringsLagerRepository: SimuleringsLagerRepository

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun lagreINyTransaksjon(simuleringsLager: SimuleringsLager) {
        simuleringsLagerRepository.insert(simuleringsLager)
    }

    override fun oppdater(simuleringsLager: SimuleringsLager) {
        simuleringsLagerRepository.update(simuleringsLager)
    }

    override fun finnAlleSimuleringsLager(): List<SimuleringsLager> {
        return simuleringsLagerRepository.findAll().toList()
    }


}
