package no.nav.familie.oppdrag.iverksetting

import no.nav.familie.kontrakter.felles.oppdrag.OppdragId
import no.nav.familie.kontrakter.felles.oppdrag.OppdragStatus
import no.nav.familie.oppdrag.domene.id
import no.nav.familie.oppdrag.repository.OppdragLagerRepository
import no.trygdeetaten.skjema.oppdrag.Oppdrag
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.concurrent.thread


@Service
@Profile("e2e")
class OppdragSenderMock(val oppdragLagerRepository: OppdragLagerRepository) : OppdragSender {

    @Transactional
    override fun sendOppdrag(oppdrag: Oppdrag): String {
        //Mocker ut funksjonen mottaKvitteringFraOppdrag
        mottaKvitteringFraOppdrag(oppdrag.id)

        return oppdrag.oppdrag110.fagsystemId
    }

    private fun mottaKvitteringFraOppdrag(oppdragId: OppdragId) =
            thread(start = true) {
                Thread.sleep(20000)
                oppdragLagerRepository.oppdaterStatus(oppdragId, OppdragStatus.KVITTERT_OK)
            }

    companion object {

        val LOG = LoggerFactory.getLogger(OppdragSenderMock::class.java)
    }
}