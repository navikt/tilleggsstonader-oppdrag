package no.nav.familie.oppdrag.simulering.repository

enum class FagOmrådeKode(val kode: String) {
    ORDINÆR_BARNETRYGD("BATR"),
    UTVIDET_BARNETRYGD("BAUT"),
    SMÅBARNSTILLEGG("BATRSMA"),
    EØS("BATR"),
    MANUELL_VURDERING("BATR");

    companion object {

        fun fraKode(kode: String): FagOmrådeKode {
            for (fagOmrådeKode in values()) {
                if (fagOmrådeKode.kode == kode) {
                    return fagOmrådeKode
                }
            }
            throw IllegalArgumentException("FagOmrådeKode finnes ikke for kode $kode")
        }
    }

}
