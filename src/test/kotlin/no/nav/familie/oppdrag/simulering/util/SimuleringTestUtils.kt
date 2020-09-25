package no.nav.familie.oppdrag.simulering.util

import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsperiode
import no.nav.system.os.entiteter.beregningskjema.Beregning
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaa
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaaDetaljer
import no.nav.system.os.entiteter.beregningskjema.BeregningsPeriode
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

fun lagTestUtbetalingsoppdragForFGBMedEttBarn()
        : Utbetalingsoppdrag {

    val personIdent = "12345678901"
    val vedtakDato = LocalDate.now()
    val datoFom = LocalDate.now().minusMonths(1)
    val datoTom = LocalDate.now().plusMonths(3)
    val fagsakId = "5566"
    val behandlingId = 334455L

    return Utbetalingsoppdrag(
            Utbetalingsoppdrag.KodeEndring.NY,
            "BA",
            fagsakId,
            UUID.randomUUID().toString(),
            "SAKSBEHANDLERID",
            LocalDateTime.now(),
            listOf(Utbetalingsperiode(false,
                                      null,
                                      1,
                                      null,
                                      vedtakDato,
                                      "BATR",
                                      datoFom,
                                      datoTom,
                                      BigDecimal(1054),
                                      Utbetalingsperiode.SatsType.MND,
                                      personIdent,
                                      behandlingId
            ))
    )
}

fun lagTestSimuleringResponse(): SimulerBeregningResponse {
    val beregningStoppnivaaDetaljer = lagBeregningStoppnivaaDetaljer()

    val beregningStoppnivaa = BeregningStoppnivaa()
    beregningStoppnivaa.beregningStoppnivaaDetaljer.add(beregningStoppnivaaDetaljer)

    val beregningsPeriode = BeregningsPeriode()
    beregningsPeriode.beregningStoppnivaa.add(beregningStoppnivaa)

    val beregning = Beregning()
    beregning.beregningsPeriode.add(beregningsPeriode)

    val simulerBeregningResponse = no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningResponse()
    simulerBeregningResponse.simulering = beregning

    val response = SimulerBeregningResponse()
    response.response = simulerBeregningResponse

    return response
}

private fun lagBeregningStoppnivaaDetaljer(): BeregningStoppnivaaDetaljer {
    val beregningStoppnivaaDetaljer = BeregningStoppnivaaDetaljer()
    beregningStoppnivaaDetaljer.faktiskFom = "2020-04-07"
    beregningStoppnivaaDetaljer.faktiskTom = "2020-04-30"
    beregningStoppnivaaDetaljer.belop = BigDecimal(1073)
    return beregningStoppnivaaDetaljer
}