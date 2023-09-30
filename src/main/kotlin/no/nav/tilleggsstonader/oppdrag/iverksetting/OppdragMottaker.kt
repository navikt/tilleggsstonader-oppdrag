package no.nav.tilleggsstonader.oppdrag.iverksetting

import jakarta.jms.TextMessage
import no.nav.familie.kontrakter.felles.oppdrag.OppdragStatus
import no.nav.tilleggsstonader.oppdrag.config.ApplicationConfig.Companion.LOKALE_PROFILER
import no.nav.tilleggsstonader.oppdrag.domene.id
import no.nav.tilleggsstonader.oppdrag.repository.OppdragLagerRepository
import no.nav.tilleggsstonader.oppdrag.repository.oppdragStatus
import no.trygdeetaten.skjema.oppdrag.Oppdrag
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OppdragMottaker(
    val oppdragLagerRepository: OppdragLagerRepository,
    val env: Environment,
) {

    internal var LOG = LoggerFactory.getLogger(OppdragMottaker::class.java)
    private val secureLogger = LoggerFactory.getLogger("secureLogger")

    @Transactional
    @JmsListener(destination = "\${oppdrag.mq.mottak}", containerFactory = "jmsListenerContainerFactory")
    fun mottaKvitteringFraOppdrag(melding: TextMessage) {
        try {
            behandleMelding(melding)
        } catch (e: Exception) {
            secureLogger.warn("Feilet lesing av melding=${melding.jmsMessageID}", e)
            throw e
        }
    }

    private fun behandleMelding(melding: TextMessage) {
        var svarFraOppdrag = melding.text as String
        if (!env.activeProfiles.any { it in LOKALE_PROFILER }) {
            if (svarFraOppdrag.contains("ns2:oppdrag")) {
                svarFraOppdrag = svarFraOppdrag.replace("oppdrag xmlns", "ns2:oppdrag xmlns:ns2")
            } else if (svarFraOppdrag.contains("ns6:oppdrag")) {
                svarFraOppdrag = svarFraOppdrag.replace("oppdrag xmlns", "ns6:oppdrag xmlns:ns6")
            }
        }

        val kvittering = lesKvittering(svarFraOppdrag)
        val oppdragId = kvittering.id
        LOG.info("Mottatt melding på kvitteringskø for fagsak $oppdragId: Status ${kvittering.status}, se securelogg for beskrivende melding")
        secureLogger.info(
            "Mottatt melding på kvitteringskø for fagsak $oppdragId: Status ${kvittering.status}, " +
                "svar ${kvittering.mmel?.beskrMelding ?: "Beskrivende melding ikke satt fra OS"}",
        )

        LOG.debug("Henter oppdrag $oppdragId fra databasen")

        val førsteOppdragUtenKvittering = oppdragLagerRepository.hentKvitteringsinformasjon(oppdragId)
            .find { oppdrag -> oppdrag.status == OppdragStatus.LAGT_PÅ_KØ }
        if (førsteOppdragUtenKvittering == null) {
            LOG.warn("Oppdraget tilknyttet mottatt kvittering har uventet status i databasen. Oppdraget er: $oppdragId")
            return
        }

        val oppdatertkvitteringsmelding = kvittering.mmel ?: førsteOppdragUtenKvittering.kvitteringsmelding
        val status = hentStatus(kvittering)
        LOG.debug("Lagrer oppdatert oppdrag $oppdragId i databasen med ny status $status")
        oppdragLagerRepository.oppdaterKvitteringsmelding(
            oppdragId = oppdragId,
            oppdragStatus = status,
            kvittering = oppdatertkvitteringsmelding,
            versjon = førsteOppdragUtenKvittering.versjon,
        )
    }

    /**
     * Lokalt settes status alltid til KVITTER_OK
     */
    private fun hentStatus(kvittering: Oppdrag) =
        if (!env.activeProfiles.contains("dev")) {
            kvittering.oppdragStatus
        } else {
            OppdragStatus.KVITTERT_OK
        }

    fun lesKvittering(svarFraOppdrag: String): Oppdrag {
        return Jaxb.tilOppdrag(svarFraOppdrag)
    }
}
