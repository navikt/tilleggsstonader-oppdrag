package no.nav.familie.oppdrag.simulering.repository

import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

data class SimuleringGrunnlag(val id: Long? = null,
                              val fagsystem: String,
                              @Column("fagsak_id") val fagsakId: String,
                              @Column("behandling_id") val behandlingId: String,
                              @Column("person_ident") val personIdent: String,
                              @Column("aktiv") var isAktiv: Boolean,
                              val versjon: Int = 0,
                              @Column("ytelse_type") val ytelseType: String = "",
                              @Column("simulering_kjoert_dato") val simuleringKjørtDato: LocalDateTime? = LocalDateTime.now()) {

    override fun toString(): String {
        return (javaClass.simpleName + "<id=" + id
                + ", behandlingId=" + behandlingId
                + ", aktiv=" + isAktiv
                + ", ytelseType=" + ytelseType
                + ", versjon=" + versjon
                + ">")
    }
}
