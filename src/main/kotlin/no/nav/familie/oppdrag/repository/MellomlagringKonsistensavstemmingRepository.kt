package no.nav.familie.oppdrag.repository

import no.nav.familie.oppdrag.service.Fagsystem
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MellomlagringKonsistensavstemmingRepository :
    RepositoryInterface<MellomlagringKonsistensavstemming, UUID>,
    InsertUpdateRepository<MellomlagringKonsistensavstemming> {

    fun findAllByFagsystemAndAvstemmingstidspunktAndAktiv(
        fagsystem: Fagsystem,
        avstemmingstidspunkt: String,
        aktiv: Boolean
    ): List<MellomlagringKonsistensavstemming>

    @Query(
        "SELECT COALESCE(sum(antall_oppdrag),0) from mellomlagring_konsistensavstemming " +
            "WHERE fagsystem = :fagsystem AND avstemmingstidspunkt = :avstemmingstidspunkt AND aktiv = true"
    )
    fun hentAggregertAntallOppdrag(fagsystem: Fagsystem, avstemmingstidspunkt: String): Int

    @Query(
        "SELECT COALESCE(sum(total_belop),0) from mellomlagring_konsistensavstemming " +
            "WHERE fagsystem = :fagsystem AND avstemmingstidspunkt = :avstemmingstidspunkt AND aktiv = true"
    )
    fun hentAggregertTotalBeløp(fagsystem: Fagsystem, avstemmingstidspunkt: String): Long
}
