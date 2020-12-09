package no.nav.familie.oppdrag.simulering.repository

import org.springframework.data.relational.core.mapping.Column
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class SimulertPostering(
        val id: Long? = null,
        @Column("fag_omraade_kode") val fagOmrådeKode: FagOmrådeKode,
        @Column("fom") val fom: LocalDate,
        @Column("tom") val tom: LocalDate,
        @Column("betaling_type") val betalingType: BetalingType,
        @Column("beloep") val beløp: BigDecimal,
        @Column("postering_type") val posteringType: PosteringType,
        @Column("simulering_mottaker_id") var simuleringMottaker: SimuleringMottaker,
        @Column("forfall") val forfallsdato: LocalDate,
        @Column("uten_inntrekk") val utenInntrekk: Boolean = false,
        @Column("opprettet_tidspunkt") val opprettetTidspunkt: LocalDateTime = LocalDateTime.now(),
) {

    fun erUtenInntrekk(): Boolean {
        return utenInntrekk
    }

    override fun toString(): String {
        return (javaClass.simpleName + "<id=" + id
                + (if (fagOmrådeKode != null) ", fagOmrådeKode=" + fagOmrådeKode.kode else "")
                + ", fom=" + fom
                + ", tom=" + tom
                + (if (betalingType != null) ", betalingType=" + betalingType.kode else "")
                + ", beløp=" + beløp
                + (if (posteringType != null) ", posteringType=" + posteringType.kode else "")
                + ", utenInntrekk=" + utenInntrekk
                + ">")
    }
}
