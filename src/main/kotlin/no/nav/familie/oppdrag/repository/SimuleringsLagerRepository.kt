package no.nav.familie.oppdrag.repository

import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SimuleringsLagerRepository: RepositoryInterface<SimuleringsLager, UUID>, InsertUpdateRepository<SimuleringsLager> {
}
