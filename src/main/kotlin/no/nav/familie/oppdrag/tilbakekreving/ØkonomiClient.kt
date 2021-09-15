package no.nav.familie.oppdrag.tilbakekreving

import no.nav.familie.oppdrag.config.IntegrasjonException
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljRequest
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljResponse
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingPortType
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakRequest
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.UUID
import javax.xml.ws.soap.SOAPFaultException

@Service
class ØkonomiClient(private val økonomiService: TilbakekrevingPortType) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val secureLogger = LoggerFactory.getLogger("secureLogger")


    fun iverksettVedtak(behandlingId: UUID,
                        tilbakekrevingsvedtakRequest: TilbakekrevingsvedtakRequest): TilbakekrevingsvedtakResponse {
        logger.info("Iverksetter vedtak for tilbakekrevingsbehandling $behandlingId")
        try {
            return økonomiService.tilbakekrevingsvedtak(tilbakekrevingsvedtakRequest)
        } catch (exception: Exception) {
            logSoapFaultException(exception)
            throw IntegrasjonException(msg = "Noe gikk galt ved iverksetting av tilbakekrevingsbehandling=$behandlingId",
                                       throwable = exception)
        }
    }

    fun hentKravgrunnlag(kravgrunnlagId: BigInteger,
                         hentKravgrunnlagRequest: KravgrunnlagHentDetaljRequest)
            : KravgrunnlagHentDetaljResponse {
        logger.info("Henter kravgrunnlag for kravgrunnlagId $kravgrunnlagId")
        try {
            return økonomiService.kravgrunnlagHentDetalj(hentKravgrunnlagRequest)
        } catch (exception: Exception) {
            logSoapFaultException(exception)
            throw IntegrasjonException(msg = "Noe gikk galt ved henting av kravgrunnlag for kravgrunnlagId=$kravgrunnlagId",
                                       throwable = exception)
        }
    }

    private fun logSoapFaultException(e: Exception) {
        if (e is SOAPFaultException) {
            secureLogger.error("SOAPFaultException -" +
                               " faultCode=${e.fault.faultCode}" +
                               " faultString=${e.fault.faultString}"
            )
        }
    }

}
