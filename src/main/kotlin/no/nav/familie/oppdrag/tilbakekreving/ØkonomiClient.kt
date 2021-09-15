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

    fun iverksettVedtak(behandlingId: UUID,
                        tilbakekrevingsvedtakRequest: TilbakekrevingsvedtakRequest)
            : TilbakekrevingsvedtakResponse {
        logger.info("Iverksetter vedtak for tilbakekrevingsbehandling $behandlingId")
        try {
            return økonomiService.tilbakekrevingsvedtak(tilbakekrevingsvedtakRequest)
        } catch (exception: SOAPFaultException) {
            logger.error("tilbakekrevingsvedtak kan ikke sende til økonomi for tilbakekrevingsbehandling=$behandlingId. " +
                         "Feiler med ${exception.message}")
            throw IntegrasjonException(msg = "Fikk feil fra økonomi ved iverksetting av tilbakekrevingsbehandling=$behandlingId",
                                       throwable = exception)
        } catch (exception: Exception) {
            logger.error("tilbakekrevingsvedtak kan ikke sende til økonomi for tilbakekrevingsbehandling=$behandlingId. " +
                         "Feiler med ${exception.message}")
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
        } catch (exception: SOAPFaultException) {
            logger.error("Kravgrunnlag kan ikke hentes fra økonomi for behandling=$kravgrunnlagId. " +
                         "Feiler med ${exception.fault.detail.firstChild.textContent}")
            throw IntegrasjonException(msg = "Kravgrunnlag kan ikke hentes fra økonomi for kravgrunnlagId=$kravgrunnlagId",
                                       throwable = exception)
        } catch (exception: Exception) {
            logger.error("Kravgrunnlag kan ikke hentes fra økonomi for behandling=$kravgrunnlagId. " +
                         "Feiler med ${exception.message}")
            throw IntegrasjonException(msg = "Noe gikk galt ved henting av kravgrunnlag for kravgrunnlagId=$kravgrunnlagId",
                                       throwable = exception)
        }
    }

}
