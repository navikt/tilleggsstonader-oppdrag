package no.nav.familie.oppdrag.konsistensavstemming

import no.nav.familie.kontrakter.felles.oppdrag.Opphør
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsperiode
import no.nav.familie.oppdrag.avstemming.SystemKode
import no.nav.familie.oppdrag.iverksetting.OppdragSkjemaConstants
import no.nav.familie.oppdrag.iverksetting.SatsTypeKode
import no.nav.familie.oppdrag.iverksetting.UtbetalingsfrekvensKode
import no.nav.familie.oppdrag.rest.PeriodeIdnForFagsak
import no.nav.familie.oppdrag.util.TestOppdragMedAvstemmingsdato
import no.nav.virksomhet.tjenester.avstemming.informasjon.konsistensavstemmingsdata.v1.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals


class KonsistensavstemmingMapper2Test {

    val fagområde = "BA"
    val idag = LocalDateTime.now()
    val tidspunktFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS")
    val datoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val saksnummer = "1"
    val saksnummer2 = "2"

    val utbetalingsoppdrag1_1 = lagUtbetalingsoppdrag(saksnummer,
                                                      lagUtbetalingsperiode(periodeId = 1,
                                                                         forrigePeriodeId = null,
                                                                         beløp = 100,
                                                                         behandlingsId = 1,
                                                                         opphør = null),
                                                      lagUtbetalingsperiode(periodeId = 2,
                                                                         forrigePeriodeId = 1,
                                                                         beløp = 100,
                                                                         behandlingsId = 1,
                                                                         opphør = null))

    // Opphør på periode 2, ny periode med annet beløp
    val utbetalingsoppdrag1_2 = lagUtbetalingsoppdrag(saksnummer,
                                                      lagUtbetalingsperiode(periodeId = 2,
                                                                          forrigePeriodeId = 1,
                                                                          beløp = 100,
                                                                          behandlingsId = 1,
                                                                          opphør = Opphør(LocalDate.now())),
                                                      lagUtbetalingsperiode(periodeId = 3,
                                                                          forrigePeriodeId = 2,
                                                                          beløp = 400,
                                                                          behandlingsId = 2,
                                                                          opphør = null))
    val utbetalingsoppdrag2_1 = lagUtbetalingsoppdrag(saksnummer2,
                                                      lagUtbetalingsperiode(periodeId = 1,
                                                                          forrigePeriodeId = null,
                                                                          beløp = 20,
                                                                          behandlingsId = 3,
                                                                          opphør = Opphør(LocalDate.now())),
                                                      lagUtbetalingsperiode(periodeId = 2,
                                                                          forrigePeriodeId = 1,
                                                                          beløp = 30,
                                                                          behandlingsId = 3,
                                                                          opphør = null))


    @Test
    internal fun `plukker ut periode fra ett utbetalingsoppdrag`() {
        val mapper = KonsistensavstemmingMapper(fagområde,
                                                listOf(utbetalingsoppdrag1_1, utbetalingsoppdrag1_2),
                                                listOf(PeriodeIdnForFagsak(saksnummer, setOf(3))), idag)
        val meldinger = mapper.lagAvstemmingsmeldingerV2()
        assertThat(meldinger).hasSize(4)
        assertThat(meldinger[1].oppdragsdataListe).hasSize(1)
        assertThat(meldinger[1].oppdragsdataListe[0].oppdragslinjeListe).hasSize(1)
        assertThat(meldinger[2].totaldata.totalBelop.toInt()).isEqualTo(400)
        assertThat(meldinger[2].totaldata.totalAntall).isEqualTo(1)
    }

    @Test
    internal fun `plukker ut perioder fra 2 utbetalingsoppdrag til en melding`() {
        val mapper = KonsistensavstemmingMapper(fagområde,
                                                listOf(utbetalingsoppdrag1_1, utbetalingsoppdrag1_2),
                                                listOf(PeriodeIdnForFagsak(saksnummer, setOf(1, 3))), idag)
        val meldinger = mapper.lagAvstemmingsmeldingerV2()
        assertThat(meldinger).hasSize(4)
        assertThat(meldinger[1].oppdragsdataListe).hasSize(1)
        assertThat(meldinger[1].oppdragsdataListe[0].oppdragslinjeListe).hasSize(2)
        assertThat(meldinger[2].totaldata.totalBelop.toInt()).isEqualTo(500)
        assertThat(meldinger[2].totaldata.totalAntall).isEqualTo(2)
    }

    @Test
    internal fun `2 ulike fagsaker med samme periodeIdn`() {
        val mapper = KonsistensavstemmingMapper(fagområde,
                                                listOf(utbetalingsoppdrag1_1, utbetalingsoppdrag1_2, utbetalingsoppdrag2_1),
                                                listOf(PeriodeIdnForFagsak(saksnummer, setOf(1)),
                                                       PeriodeIdnForFagsak(saksnummer2, setOf(1))),
                                                idag)
        val meldinger = mapper.lagAvstemmingsmeldingerV2()
        assertThat(meldinger).hasSize(5)
        listOf(1,2).forEach {
            assertThat(meldinger[it].oppdragsdataListe).hasSize(1)
            assertThat(meldinger[it].oppdragsdataListe[0].oppdragslinjeListe).hasSize(1)
        }
        assertThat(meldinger[3].totaldata.totalBelop.toInt()).isEqualTo(120)
        assertThat(meldinger[3].totaldata.totalAntall).isEqualTo(2)
    }

    private fun lagUtbetalingsperiode(periodeId: Long,
                                      forrigePeriodeId: Long?,
                                      beløp: Int,
                                      behandlingsId: Long,
                                      opphør: Opphør? = null) =
            Utbetalingsperiode(erEndringPåEksisterendePeriode = false,
                               opphør = opphør,
                               periodeId = periodeId,
                               forrigePeriodeId = forrigePeriodeId,
                               datoForVedtak = LocalDate.now(),
                               klassifisering = "EF",
                               vedtakdatoFom = LocalDate.now().minusYears(1),
                               vedtakdatoTom = LocalDate.now().plusYears(1),
                               sats = BigDecimal(beløp),
                               satsType = Utbetalingsperiode.SatsType.MND,
                               utbetalesTil = "meg",
                               behandlingId = behandlingsId)

    private fun lagUtbetalingsoppdrag(saksnummer: String, vararg utbetalingsperiode: Utbetalingsperiode) =
            Utbetalingsoppdrag(kodeEndring = Utbetalingsoppdrag.KodeEndring.NY,
                               fagSystem = fagområde,
                               saksnummer = saksnummer,
                               aktoer = "aktoer",
                               saksbehandlerId = "saksbehandler",
                               utbetalingsperiode = utbetalingsperiode.toList())

    @Test
    fun tester_at_det_mappes_riktig_til_konsistensavstemming() {
        val utbetalingsoppdrag = TestOppdragMedAvstemmingsdato.lagTestUtbetalingsoppdrag(idag, fagområde)
        val mapper = KonsistensavstemmingMapper(fagområde, listOf(utbetalingsoppdrag), emptyList(), idag)
        val meldinger = mapper.lagAvstemmingsmeldinger()
        assertEquals(4, meldinger.size)
        // START-meldingen
        assertAksjon(KonsistensavstemmingConstants.START, meldinger.first().aksjonsdata)
        // DATA-meldingen
        assertAksjon(KonsistensavstemmingConstants.DATA, meldinger[1].aksjonsdata)
        assertOppdragsdata(utbetalingsoppdrag, meldinger[1].oppdragsdataListe.first())
        // TOTALDATA-meldingen
        assertAksjon(KonsistensavstemmingConstants.DATA, meldinger[2].aksjonsdata)
        assertTotaldata(utbetalingsoppdrag.utbetalingsperiode.first(), meldinger[2].totaldata)
        // AVSLUTT-meldingen
        assertAksjon(KonsistensavstemmingConstants.AVSLUTT, meldinger.last().aksjonsdata)
    }

    @Test
    fun totaldata_skal_akkumuleres_riktig() {
        val utbetalingsoppdrag = TestOppdragMedAvstemmingsdato.lagTestUtbetalingsoppdrag(idag, fagområde)
        val utbetalingsoppdrag2 = TestOppdragMedAvstemmingsdato.lagTestUtbetalingsoppdrag(idag, fagområde)
        val mapper = KonsistensavstemmingMapper(fagområde, listOf(utbetalingsoppdrag, utbetalingsoppdrag2), emptyList(), idag)
        val meldinger = mapper.lagAvstemmingsmeldinger()
        assertEquals(5, meldinger.size)
        assertEquals(KonsistensavstemmingConstants.DATA, meldinger[3].aksjonsdata.aksjonsType)
        assertEquals(BigInteger.TWO, meldinger[3].totaldata.totalAntall)
    }

    @Test
    fun totaldata_skal_ikke_akkumulere_opp_utbetalingsperioder_som_har_passert() {
        val utbetalingsoppdrag = TestOppdragMedAvstemmingsdato.lagTestUtbetalingsoppdrag(idag.plusYears(7), fagområde)
        val utbetalingsoppdrag2 = TestOppdragMedAvstemmingsdato.lagTestUtbetalingsoppdragMedPeriode(idag.plusYears(7),
                                                                                                    fagområde,
                                                                                                    LocalDate.now()
                                                                                                            .plusYears(6)
                                                                                                            .withDayOfMonth(1),
                                                                                                    LocalDate.now().plusYears(12))
        val mapper = KonsistensavstemmingMapper(fagområde,
                                                listOf(utbetalingsoppdrag, utbetalingsoppdrag2),
                                                emptyList(),
                                                idag.plusYears(7))
        val meldinger = mapper.lagAvstemmingsmeldinger()
        assertEquals(5, meldinger.size)
        assertEquals(KonsistensavstemmingConstants.DATA, meldinger[3].aksjonsdata.aksjonsType)
        assertEquals(BigInteger.ONE, meldinger[3].totaldata.totalAntall)
    }

    fun assertAksjon(expected: String, actual: Aksjonsdata) {
        assertEquals(expected, actual.aksjonsType)
        assertEquals(KonsistensavstemmingConstants.KILDETYPE, actual.kildeType)
        assertEquals(KonsistensavstemmingConstants.KONSISTENSAVSTEMMING, actual.avstemmingType)
        assertEquals(fagområde, actual.avleverendeKomponentKode)
        assertEquals(SystemKode.OPPDRAGSSYSTEMET.kode, actual.mottakendeKomponentKode)
        assertEquals(fagområde, actual.underkomponentKode)
        assertEquals(idag.format(tidspunktFormatter), actual.tidspunktAvstemmingTom)
        assertEquals(fagområde, actual.brukerId)
    }

    fun assertTotaldata(utbetalingsperiode: Utbetalingsperiode, actual: Totaldata) {
        assertEquals(BigInteger.ONE, actual.totalAntall)
        assertEquals(utbetalingsperiode.sats, actual.totalBelop)
        assertEquals(KonsistensavstemmingConstants.FORTEGN_T, actual.fortegn)
    }

    fun assertOppdragsdata(utbetalingsoppdrag: Utbetalingsoppdrag, actual: Oppdragsdata) {
        assertEquals(fagområde, actual.fagomradeKode)
        assertEquals(utbetalingsoppdrag.saksnummer, actual.fagsystemId)
        assertEquals(UtbetalingsfrekvensKode.MÅNEDLIG.kode, actual.utbetalingsfrekvens)
        assertEquals(utbetalingsoppdrag.aktoer, actual.oppdragGjelderId)
        assertEquals(OppdragSkjemaConstants.OPPDRAG_GJELDER_DATO_FOM.format(datoFormatter), actual.oppdragGjelderFom)
        assertEquals(utbetalingsoppdrag.saksbehandlerId, actual.saksbehandlerId)
        assertEnhet(actual.oppdragsenhetListe.first())
        assertOppdragsLinjeListe(utbetalingsoppdrag.utbetalingsperiode.first(),
                                 utbetalingsoppdrag.saksbehandlerId,
                                 actual.oppdragslinjeListe.first())
    }

    fun assertOppdragsLinjeListe(utbetalingsperiode: Utbetalingsperiode, saksbehandler: String, actual: Oppdragslinje) {
        assertEquals(utbetalingsperiode.datoForVedtak.format(datoFormatter), actual.vedtakId)
        assertEquals(utbetalingsperiode.klassifisering, actual.klassifikasjonKode)
        assertEquals(utbetalingsperiode.vedtakdatoFom.format(datoFormatter), actual.vedtakPeriode.fom)
        assertEquals(utbetalingsperiode.vedtakdatoTom.format(datoFormatter), actual.vedtakPeriode.tom)
        assertEquals(utbetalingsperiode.sats, actual.sats)
        assertEquals(SatsTypeKode.fromKode(utbetalingsperiode.satsType.name).kode, actual.satstypeKode)
        assertEquals(OppdragSkjemaConstants.BRUK_KJØREPLAN, actual.brukKjoreplan)
        assertEquals(OppdragSkjemaConstants.FRADRAG_TILLEGG.value(), actual.fradragTillegg)
        assertEquals(saksbehandler, actual.saksbehandlerId)
        assertEquals(utbetalingsperiode.utbetalesTil, actual.utbetalesTilId)
        assertEquals(utbetalingsperiode.behandlingId.toString(), actual.henvisning)
        assertEquals(saksbehandler, actual.attestantListe.first().attestantId)
    }

    fun assertEnhet(enhet: Enhet) {
        assertEquals(OppdragSkjemaConstants.ENHET_TYPE, enhet.enhetType)
        assertEquals(OppdragSkjemaConstants.ENHET, enhet.enhet)
        assertEquals(OppdragSkjemaConstants.ENHET_DATO_FOM.format(datoFormatter), enhet.enhetFom)
    }

}