package no.nav.familie.oppdrag.simulering.repository

import no.nav.familie.oppdrag.service.Fagsystem

enum class FagOmrådeKode(val kode: String) {
    ORDINÆR_BARNETRYGD("BATR"),
    UTVIDET_BARNETRYGD("BAUT"),
    SMÅBARNSTILLEGG("BATRSMA"),
    EØS("BATR"),
    MANUELL_VURDERING("BATR")
}
