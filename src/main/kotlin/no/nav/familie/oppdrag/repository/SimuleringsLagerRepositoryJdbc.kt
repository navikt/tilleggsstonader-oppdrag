package no.nav.familie.oppdrag.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SimuleringsLagerRepositoryJdbc(val jdbcTemplate: JdbcTemplate) : SimuleringsLagerRepository {

    override fun opprettSimulering(simuleringsLager: SimuleringsLager, versjon: Int) {
        val insertStatement = "INSERT INTO simulerings_lager " +
                              "(request_xml, response_xml, opprettet_tidspunkt, person_ident, fagsak_id, behandling_id, fagsystem, utbetalingsoppdrag, versjon)" +
                              " VALUES (?,?,?,?,?,?,?,?,?)"

        jdbcTemplate.update(insertStatement,
                            simuleringsLager.requestXml,
                            simuleringsLager.responseXml,
                            simuleringsLager.opprettetTidspunkt,
                            simuleringsLager.personIdent,
                            simuleringsLager.fagsakId,
                            simuleringsLager.behandlingId,
                            simuleringsLager.fagsystem,
                            simuleringsLager.utbetalingsoppdrag,
                            versjon)
    }
}
