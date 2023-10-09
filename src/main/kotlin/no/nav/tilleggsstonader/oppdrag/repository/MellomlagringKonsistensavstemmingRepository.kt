package no.nav.tilleggsstonader.oppdrag.repository

import no.nav.tilleggsstonader.oppdrag.infrastruktur.database.InsertUpdateRepository
import no.nav.tilleggsstonader.oppdrag.infrastruktur.database.RepositoryInterface
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MellomlagringKonsistensavstemmingRepository :
    RepositoryInterface<MellomlagringKonsistensavstemming, UUID>,
        InsertUpdateRepository<MellomlagringKonsistensavstemming> {

    fun findAllByTransaksjonsId(
        transaksjonsId: UUID,
    ): List<MellomlagringKonsistensavstemming>

    @Query(
        "SELECT COALESCE(sum(antall_oppdrag),0) from mellomlagring_konsistensavstemming WHERE transaksjons_id = :transaksjonsId",
    )
    fun hentAggregertAntallOppdrag(transaksjonsId: UUID): Int

    @Query(
        "SELECT COALESCE(sum(total_belop),0) from mellomlagring_konsistensavstemming WHERE transaksjons_id = :transaksjonsId",
    )
    fun hentAggregertTotalBeløp(transaksjonsId: UUID): Long
}
