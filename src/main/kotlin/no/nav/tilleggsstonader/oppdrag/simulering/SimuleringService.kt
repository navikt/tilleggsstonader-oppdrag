package no.nav.tilleggsstonader.oppdrag.simulering

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.kontrakter.felles.simulering.DetaljertSimuleringResultat
import no.nav.familie.kontrakter.felles.simulering.FeilutbetalingerFraSimulering
import no.nav.familie.kontrakter.felles.simulering.FeilutbetaltPeriode
import no.nav.familie.kontrakter.felles.simulering.HentFeilutbetalingerFraSimuleringRequest
import no.nav.familie.kontrakter.felles.simulering.PosteringType
import no.nav.system.os.entiteter.beregningskjema.Beregning
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaaDetaljer
import no.nav.system.os.entiteter.beregningskjema.BeregningsPeriode
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningFeilUnderBehandling
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import no.nav.tilleggsstonader.oppdrag.common.Jaxb
import no.nav.tilleggsstonader.oppdrag.common.logSoapFaultException
import no.nav.tilleggsstonader.oppdrag.infrastruktur.config.FinnesIkkeITps
import no.nav.tilleggsstonader.oppdrag.infrastruktur.config.IntegrasjonException
import no.nav.tilleggsstonader.oppdrag.infrastruktur.config.Integrasjonssystem
import no.nav.tilleggsstonader.oppdrag.simulering.domain.SimuleringLager
import no.nav.tilleggsstonader.oppdrag.simulering.domain.SimuleringLagerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class SimuleringService(
    val simuleringSender: SimuleringSender,
    val simulerBeregningRequestMapper: SimulerBeregningRequestMapper,
    val simuleringLagerRepository: SimuleringLagerRepository,
) {

    val mapper = jacksonObjectMapper()
    val simuleringResultatTransformer = SimuleringResultatTransformer()

    private fun hentSimulerBeregningResponse(
        simulerBeregningRequest: SimulerBeregningRequest,
        utbetalingsoppdrag: Utbetalingsoppdrag,
    ): SimulerBeregningResponse {
        try {
            val response = simuleringSender.hentSimulerBeregningResponse(simulerBeregningRequest)
            secureLogger.info(
                "Saksnummer: ${utbetalingsoppdrag.saksnummer} : " +
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response),
            )
            return response
        } catch (ex: SimulerBeregningFeilUnderBehandling) {
            val feilmelding = genererFeilmelding(ex)
            if (feilmelding.contains("Personen finnes ikke i TPS")) {
                throw FinnesIkkeITps(Integrasjonssystem.SIMULERING)
            }
            throw IntegrasjonException(Integrasjonssystem.SIMULERING, feilmelding, ex)
        } catch (ex: Exception) {
            logSoapFaultException(ex)
            throw IntegrasjonException(Integrasjonssystem.SIMULERING, "Ukjent feil mot simulering", ex)
        }
    }

    fun utførSimuleringOghentDetaljertSimuleringResultat(utbetalingsoppdrag: Utbetalingsoppdrag): DetaljertSimuleringResultat {
        val simulerBeregningRequest = simulerBeregningRequestMapper.tilSimulerBeregningRequest(utbetalingsoppdrag)

        secureLogger.info(
            "Saksnummer: ${utbetalingsoppdrag.saksnummer} : " +
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(simulerBeregningRequest),
        )

        val simuleringsLager = SimuleringLager.lagFraOppdrag(utbetalingsoppdrag, simulerBeregningRequest)
        simuleringLagerRepository.insert(simuleringsLager)

        val respons = hentSimulerBeregningResponse(simulerBeregningRequest, utbetalingsoppdrag)

        simuleringsLager.responseXml = Jaxb.tilXml(respons)
        simuleringLagerRepository.update(simuleringsLager)

        val beregning = respons.response?.simulering ?: return DetaljertSimuleringResultat(emptyList())
        return simuleringResultatTransformer.mapSimulering(
            beregning = beregning,
            utbetalingsoppdrag = utbetalingsoppdrag,
        )
    }

    fun hentFeilutbetalinger(request: HentFeilutbetalingerFraSimuleringRequest): FeilutbetalingerFraSimulering {
        val simuleringLager = simuleringLagerRepository.finnSisteSimuleringsresultat(
            request.ytelsestype.kode,
            request.eksternFagsakId,
            request.fagsystemsbehandlingId,
        )
        val respons = Jaxb.tilSimuleringsrespons(simuleringLager.responseXml!!)
        val simulering = respons.response.simulering

        val feilPosteringerMedPositivBeløp = finnFeilPosteringer(simulering)
        val alleYtelPosteringer = finnYtelPosteringer(simulering)

        val feilutbetaltPerioder = feilPosteringerMedPositivBeløp.map { feilPostering ->
            val periode = feilPostering.key
            val feilutbetaltBeløp = feilPostering.value.sumOf { it.belop }
            val ytelPosteringerForPeriode = hentYtelPerioder(periode, alleYtelPosteringer)
            FeilutbetaltPeriode(
                fom = LocalDate.parse(periode.periodeFom),
                tom = LocalDate.parse(periode.periodeTom),
                feilutbetaltBeløp = feilutbetaltBeløp,
                tidligereUtbetaltBeløp = summerNegativeYtelPosteringer(
                    ytelPosteringerForPeriode,
                    alleYtelPosteringer,
                ).abs(),
                nyttBeløp = summerPostiveYtelPosteringer(
                    ytelPosteringerForPeriode,
                    alleYtelPosteringer,
                ) - feilutbetaltBeløp,
            )
        }
        return FeilutbetalingerFraSimulering(feilutbetaltePerioder = feilutbetaltPerioder)
    }

    private fun finnFeilPosteringer(simulering: Beregning): Map<BeregningsPeriode, List<BeregningStoppnivaaDetaljer>> {
        return simulering.beregningsPeriode.map { beregningsperiode ->
            beregningsperiode to beregningsperiode.beregningStoppnivaa.map { stoppNivå ->
                stoppNivå.beregningStoppnivaaDetaljer.filter { detalj ->
                    detalj.typeKlasse == PosteringType.FEILUTBETALING.kode &&
                        detalj.belop > BigDecimal.ZERO
                }
            }.flatten()
        }.filter { it.second.isNotEmpty() }.toMap()
    }

    private fun finnYtelPosteringer(simulering: Beregning): Map<BeregningsPeriode, List<BeregningStoppnivaaDetaljer>> {
        return simulering.beregningsPeriode.associateWith { beregningsperiode ->
            beregningsperiode.beregningStoppnivaa.map { stoppNivå ->
                stoppNivå.beregningStoppnivaaDetaljer.filter { detalj ->
                    detalj.typeKlasse == PosteringType.YTELSE.kode
                }
            }.flatten()
        }
    }

    private fun hentYtelPerioder(
        feilutbetaltePeriode: BeregningsPeriode,
        ytelPerioder: Map<BeregningsPeriode, List<BeregningStoppnivaaDetaljer>>,
    ): List<BeregningsPeriode> {
        return ytelPerioder.keys.filter { ytelPeriode ->
            ytelPeriode.periodeFom == feilutbetaltePeriode.periodeFom &&
                ytelPeriode.periodeTom == feilutbetaltePeriode.periodeTom
        }
    }

    private fun summerNegativeYtelPosteringer(
        perioder: List<BeregningsPeriode>,
        ytelPerioder: Map<BeregningsPeriode, List<BeregningStoppnivaaDetaljer>>,
    ) =
        perioder.sumOf { beregningsperiode ->
            ytelPerioder.getValue(beregningsperiode).filter { it.belop < BigDecimal.ZERO }
                .sumOf { detalj -> detalj.belop }
        }

    private fun summerPostiveYtelPosteringer(
        perioder: List<BeregningsPeriode>,
        ytelPerioder: Map<BeregningsPeriode, List<BeregningStoppnivaaDetaljer>>,
    ) =
        perioder.sumOf { beregningsperiode ->
            ytelPerioder.getValue(beregningsperiode).filter { it.belop > BigDecimal.ZERO }
                .sumOf { detalj -> detalj.belop }
        }

    private fun genererFeilmelding(ex: SimulerBeregningFeilUnderBehandling): String =
        ex.faultInfo.let {
            "Feil ved hentSimulering (SimulerBeregningFeilUnderBehandling) " +
                "source: ${it.errorSource}, " +
                "type: ${it.errorType}, " +
                "message: ${it.errorMessage}, " +
                "rootCause: ${it.rootCause}, " +
                "rootCause: ${it.dateTimeStamp}"
        }

    companion object {

        val secureLogger: Logger = LoggerFactory.getLogger("secureLogger")
    }
}
