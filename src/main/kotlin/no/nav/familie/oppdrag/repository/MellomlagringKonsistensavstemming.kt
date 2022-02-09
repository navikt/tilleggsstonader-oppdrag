package no.nav.familie.oppdrag.repository

import no.nav.familie.oppdrag.service.Fagsystem
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Table("mellomlagring_konsistensavstemming")
data class MellomlagringKonsistensavstemming(
    @Id val id: UUID = UUID.randomUUID(),
    val fagsystem: Fagsystem,
    @Column("avstemmingstidspunkt") val avstemmingstidspunkt: String,
    @Column("aktiv") var aktiv: Boolean = true,
    @Column("antall_oppdrag") val antallOppdrag: Int,
    @Column("total_belop") val totalBeløp: Long,
    @Column("opprettet_tidspunkt") val opprettetTidspunkt: LocalDateTime = LocalDateTime.now(),
    @Column("endret_tidspunkt") val endretTidspunkt: LocalDateTime? = null
) {
    companion object {
        val avstemingstidspunktFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}

