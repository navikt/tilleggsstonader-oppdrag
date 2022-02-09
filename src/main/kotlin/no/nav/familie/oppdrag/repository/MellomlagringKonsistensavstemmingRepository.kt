package no.nav.familie.oppdrag.repository

import no.nav.familie.oppdrag.service.Fagsystem
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface MellomlagringKonsistensavstemmingRepository :
    RepositoryInterface<MellomlagringKonsistensavstemming, UUID>,
    InsertUpdateRepository<MellomlagringKonsistensavstemming> {

    fun findByFagsystemAndAvstemmingstidspunkt(fagsystem: Fagsystem, avstemmingstidspunkt: LocalDateTime):
        List<MellomlagringKonsistensavstemming>

    @Query(
        "UPDATE MellomlagringKonsistensavstemming SET aktiv = false " +
            "WHERE fagsystem = :fagsystem AND avstemmingstidspunkt = :avstemmingstidspunkt"
    )
    fun nullstillMellomlagring(fagsystem: Fagsystem, avstemmingstidspunkt: LocalDateTime)

    @Query(
        "SELECT sum(totalAntall) from MellomlagringKonsistensavstemming " +
                "WHERE fagsystem = :fagsystem AND avstemmingstidspunkt = :avstemmingstidspunkt AND aktiv = true"
    )
    fun hentAggregertAntallOppdrag(fagsystem: Fagsystem, avstemmingstidspunkt: LocalDateTime): Int

    @Query(
        "SELECT sum(totalBeløp) from MellomlagringKonsistensavstemming " +
                "WHERE fagsystem = :fagsystem AND avstemmingstidspunkt = :avstemmingstidspunkt AND aktiv = true"
    )
    fun hentaggregertTotalBeløp(fagsystem: Fagsystem, avstemmingstidspunkt: LocalDateTime): Long
}
