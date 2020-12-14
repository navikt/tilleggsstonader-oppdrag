package no.nav.familie.oppdrag.repository

import no.nav.familie.kontrakter.felles.objectMapper
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.kontrakter.felles.oppdrag.behandlingsIdForFørsteUtbetalingsperiode
import no.nav.familie.oppdrag.iverksetting.Jaxb
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

data class SimuleringsLager(val fagsystem: String,
                            @Column("person_ident") val personIdent: String,
                            @Column("fagsak_id") val fagsakId: String,
                            @Column("behandling_id") val behandlingId: String,
                            val utbetalingsoppdrag: String,
                            @Column("request_xml") val requestXml: String,
                            @Column("response_xml") val responseXml: String,
                            @Column("opprettet_tidspunkt") val opprettetTidspunkt: LocalDateTime = LocalDateTime.now(),
                            val versjon: Int = 0) {

    companion object {
        fun lagFraOppdrag(utbetalingsoppdrag: Utbetalingsoppdrag,
                          request: SimulerBeregningRequest,
                          response: SimulerBeregningResponse,
                          versjon: Int = 0): SimuleringsLager {
            return SimuleringsLager(
                    personIdent = utbetalingsoppdrag.aktoer,
                    fagsystem = utbetalingsoppdrag.fagSystem,
                    fagsakId = utbetalingsoppdrag.saksnummer,
                    behandlingId = utbetalingsoppdrag.behandlingsIdForFørsteUtbetalingsperiode(),
                    utbetalingsoppdrag = objectMapper.writeValueAsString(utbetalingsoppdrag),
                    requestXml = Jaxb.tilXml(request = request),
                    responseXml = Jaxb.tilXml(response = response),
                    versjon = versjon
            )
        }
    }
}
