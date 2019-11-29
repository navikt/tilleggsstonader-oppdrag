package no.nav.familie.oppdrag.iverksetting

import no.trygdeetaten.skjema.oppdrag.Oppdrag110


class OppdragSkjemaConstants {

    fun lagNyOppdrag110() {
        val oppdrag110 = Oppdrag110()

    }
}

enum class EndringsKode(val kode: String) {
    NY("NY"),
    UENDRET("UEND"),
    ENDRING("ENDR")
}

enum class UtbetalingsfrekvensKode(val kode: String) {
    DAGLIG("DAG"),
    UKENTLIG("UKE"),
    MÅNEDLIG("MND"),
    DAGLIG_14("14DG"),
    ENGANGSUTBETALING("ENG")
}

enum class SatsTypeKode(val kode: String) {
    DAGLIG("DAG"),
    UKENTLIG("UKE"),
    MÅNEDLIG("MND"),
    DAGLIG_14("14DG"),
    ENGANGSBELØP("ENG"),
    ÅRLIG("AAR"),
    A_KONTO("AKTO"),
    UKJENT("-");

    companion object {
        fun fromKode(kode: String): SatsTypeKode {
            for (s in values()) {
                if (s.kode == kode ) return s
            }
            return UKJENT
        }
    }
}

enum class GradTypeKode(val kode: String) {
    UFØREGRAD("UFOR"),
    UTBETALINGSGRAD("UBGR"),
    UTTAKSGRAD_ALDERSPENSJON("UTAP"),
    UTTAKSGRAD_AFP("AFPG")

}

enum class UtbetalingsType(val kode: String) {
    YTELSE("YTEL"),
    FEILUTBETALING("FEIL"),
    FORSKUDSSKATT("SKAT"),
    JUSTERING("JUST"),
    TREKK("TREK"),
    UDEFINERT("-");

    companion object {
        fun fromKode(kode: String): UtbetalingsType {
            for (u in values()) {
                if (u.kode == kode ) return u
            }
            return UDEFINERT
        }
    }
}