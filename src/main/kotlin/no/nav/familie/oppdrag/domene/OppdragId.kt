package no.nav.familie.oppdrag.domene

import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.trygdeetaten.skjema.oppdrag.Oppdrag

inline class Fagsystem(val s: String)
inline class Identifikator(val s: String)
inline class BehandlingId(val s: String)

data class OppdragId(val fagsystem : Fagsystem,
                     val fødselsnummer : Identifikator,
                     val behandlingsId : BehandlingId)

val Oppdrag.id : OppdragId
    get() = OppdragId(Fagsystem(this.oppdrag110.kodeFagomraade),
                      Identifikator(this.oppdrag110.oppdragGjelderId),
                      BehandlingId(this.oppdrag110.oppdragsLinje150?.get(0)?.henvisning!!))

val Utbetalingsoppdrag.id : OppdragId
    get() = OppdragId(Fagsystem(this.fagSystem),
                      Identifikator(this.aktoer),
                      BehandlingId(this.utbetalingsperiode.get(0).behandlingId.toString()))