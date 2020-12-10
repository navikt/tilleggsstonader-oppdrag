package no.nav.familie.oppdrag.simulering

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.familie.kontrakter.felles.oppdrag.RestSimulerResultat
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.oppdrag.service.KonsistensavstemmingService
import no.nav.familie.oppdrag.simulering.repository.DetaljertSimuleringResultat
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerBeregningFeilUnderBehandling
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.ApplicationScope

@Service
@ApplicationScope
@Profile("!e2e")
class SimuleringTjenesteImpl(@Autowired val simuleringSender: SimuleringSender,
                             @Autowired val simulerBeregningRequestMapper: SimulerBeregningRequestMapper) : SimuleringTjeneste {

    val mapper = jacksonObjectMapper()

    override fun utførSimulering(utbetalingsoppdrag: Utbetalingsoppdrag): RestSimulerResultat {
        return hentSimulerBeregningResponse(utbetalingsoppdrag).toRestSimulerResult()
    }

    override fun hentSimulerBeregningResponse(utbetalingsoppdrag: Utbetalingsoppdrag): SimulerBeregningResponse {
        val simulerBeregningRequest = simulerBeregningRequestMapper.tilSimulerBeregningRequest(utbetalingsoppdrag)

        secureLogger.info("Saksnummer: ${utbetalingsoppdrag.saksnummer} : " +
                          mapper.writerWithDefaultPrettyPrinter().writeValueAsString(simulerBeregningRequest))

        return try {
            val response = simuleringSender.hentSimulerBeregningResponse(simulerBeregningRequest)

            secureLogger.info("Saksnummer: ${utbetalingsoppdrag.saksnummer} : " +
                              mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response))

            return response
        } catch (ex: SimulerBeregningFeilUnderBehandling) {
            val feilmelding = genererFeilmelding(ex)

            LOG.info(feilmelding)
            throw Exception(feilmelding, ex)
        } catch (ex: Throwable) {
            throw Exception(ex.message, ex)
        }
    }

    override fun utførSimuleringOghentDetaljertSimuleringResultat(utbetalingsoppdrag: Utbetalingsoppdrag): DetaljertSimuleringResultat {
        val respons = hentSimulerBeregningResponse(utbetalingsoppdrag)


        return DetaljertSimuleringResultat(simuleringMottaker = listOf())
    }

    private fun genererFeilmelding(ex: SimulerBeregningFeilUnderBehandling): String =
            ex.getFaultInfo().let {
                "Feil ved hentSimulering (SimulerBeregningFeilUnderBehandling) " +
                "source: ${it.errorSource}, " +
                "type: ${it.errorType}, " +
                "message: ${it.errorMessage}, " +
                "rootCause: ${it.rootCause}, " +
                "rootCause: ${it.dateTimeStamp}"
            }

    companion object {

        val secureLogger = LoggerFactory.getLogger("secureLogger")
        val LOG: Logger = LoggerFactory.getLogger(KonsistensavstemmingService::class.java)
    }
}
