package no.nav.familie.oppdrag.iverksetting

import no.nav.familie.oppdrag.domene.id
import no.nav.familie.oppdrag.repository.OppdragProtokoll
import no.nav.familie.oppdrag.repository.OppdragProtokollRepository
import no.nav.familie.oppdrag.repository.OppdragProtokollStatus
import no.nav.familie.oppdrag.repository.protokollStatus
import no.trygdeetaten.skjema.oppdrag.Oppdrag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import javax.jms.TextMessage

@Service
class OppdragMottaker(
        @Autowired val oppdragProtokollRepository: OppdragProtokollRepository,
        val env: Environment) {

    // jmsListenerContainerFactory sørger for en transaksjon. Exception her betyr at meldingen blir liggende på køen
    @JmsListener(destination = "\${oppdrag.mq.mottak}", containerFactory = "jmsListenerContainerFactory")
    fun mottaKvitteringFraOppdrag(melding: TextMessage) {
        var svarFraOppdrag = melding.text as String
        if (!env.activeProfiles.contains("dev")) {
            svarFraOppdrag = svarFraOppdrag.replace("oppdrag xmlns", "ns2:oppdrag xmlns:ns2")
        }

        val kvittering = lesKvittering(svarFraOppdrag)
        val oppdragId = kvittering.id
        LOG.info("Mottatt melding på kvitteringskø for fagsak ${oppdragId}: Status ${kvittering.status}, svar ${hentMelding(kvittering)}")

        LOG.info("Henter oppdrag ${oppdragId} fra databasen")
        val sendteOppdrag: List<OppdragProtokoll> = oppdragProtokollRepository.hentEksisterendeOppdrag(
                oppdragId.fagsystem,
                oppdragId.behandlingsId,
                oppdragId.fødselsnummer
        )

        when {
            sendteOppdrag.size!=1 -> {
                // TODO: Fant ikke oppdrag. Det er VELDIG feil
                LOG.error("Fant ikke oppdraget knytt til kvittering: "+oppdragId)

            }
            sendteOppdrag[0].status != OppdragProtokollStatus.LAGT_PÅ_KØ -> {
                // TODO: Oppdraget har en status vi ikke venter. Det er GANSKE så feil
                LOG.warn("Oppdraget tilknyttet mottatt kvittering har uventet status i databasen. Oppdraget er: ${oppdragId}. " +
                         "Status i databasen er ${sendteOppdrag[0].status}")
            }
            else -> {
                val nyStatus = kvittering.protokollStatus
                LOG.info("Lagrer oppdatert oppdrag ${oppdragId} i databasen med ny status ${nyStatus}")
                val oppdatertOppdrag = sendteOppdrag[0].copy(status = nyStatus)
                oppdragProtokollRepository.save(oppdatertOppdrag)
            }
        }
    }

    fun lesKvittering(svarFraOppdrag: String): Oppdrag {
        val kvittering = Jaxb().tilOppdrag(svarFraOppdrag)
        return kvittering
    }

    private fun hentMelding(kvittering: Oppdrag): String {
        return kvittering.mmel?.beskrMelding ?: "Beskrivende melding ikke satt fra OS"
    }

    companion object {
        val LOG = LoggerFactory.getLogger(OppdragMottaker::class.java)
    }
}