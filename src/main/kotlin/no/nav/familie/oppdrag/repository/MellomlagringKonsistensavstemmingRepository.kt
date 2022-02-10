package no.nav.familie.oppdrag.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MellomlagringKonsistensavstemmingRepository :
    RepositoryInterface<MellomlagringKonsistensavstemming, UUID>,
    InsertUpdateRepository<MellomlagringKonsistensavstemming> {

    fun findAllByTransaksjonsId(
        transaksjonsId: String
    ): List<MellomlagringKonsistensavstemming>

    @Query(
        "SELECT COALESCE(sum(antall_oppdrag),0) from mellomlagring_konsistensavstemming WHERE transaksjons_id = :transaksjonsId"
    )
    fun hentAggregertAntallOppdrag(transaksjonsId: String): Int

    @Query(
        "SELECT COALESCE(sum(total_belop),0) from mellomlagring_konsistensavstemming WHERE transaksjons_id = :transaksjonsId"
    )
    fun hentAggregertTotalBeløp(transaksjonsId: String): Long
}
