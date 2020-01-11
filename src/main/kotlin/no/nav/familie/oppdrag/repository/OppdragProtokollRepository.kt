package no.nav.familie.oppdrag.repository

import no.nav.familie.oppdrag.domene.BehandlingId
import no.nav.familie.oppdrag.domene.Fagsystem
import no.nav.familie.oppdrag.domene.Identifikator
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface OppdragProtokollRepository : CrudRepository<OppdragProtokoll, Long> {

    @Query("SELECT * FROM OPPDRAG_PROTOKOLL WHERE behandling_id = :behandlingId AND person_ident = :personIdent AND fagsystem = :fagsystem")
    fun hentEksisterendeOppdrag(fagsystem: Fagsystem, behandlingId: BehandlingId, personIdent: Identifikator): List<OppdragProtokoll>

}