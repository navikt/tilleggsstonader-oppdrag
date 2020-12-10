package no.nav.familie.oppdrag.simulering.repository

import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

data class DetaljertSimuleringResultat (
                                        val simuleringMottaker: List<SimuleringMottaker>,
                                        )



