package no.nav.familie.oppdrag.simulering.repository

enum class PosteringType(val kode: String) {

    YTELSE("YTEL"),
    FEILUTBETALING("FEIL"),
    FORSKUDSSKATT("SKAT"),
    JUSTERING("JUST"),
}
