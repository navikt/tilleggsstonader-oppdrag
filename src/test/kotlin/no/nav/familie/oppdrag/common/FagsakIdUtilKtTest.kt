package no.nav.familie.oppdrag.common

import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class FagsakIdUtilKtTest {

    private val utbetalingsoppdrag = Utbetalingsoppdrag(kodeEndring = Utbetalingsoppdrag.KodeEndring.NY,
                                                        fagSystem = "BA",
                                                        saksnummer = "saksnummer",
                                                        aktoer = "0",
                                                        saksbehandlerId = "",
                                                        avstemmingTidspunkt = LocalDateTime.now(),
                                                        utbetalingsperiode = emptyList())

    @Test
    internal fun `skal returnere 0 i prefix hvis det er preprod`() {
        val utbetalingsoppdrag = utbetalingsoppdrag
        assertThat(utbetalingsoppdrag.fagsystemId("preprod")).isEqualTo("0saksnummer")
    }

    @Test
    internal fun `skal ikke returnere 0 i prefix hvis det er prod`() {
        val utbetalingsoppdrag = utbetalingsoppdrag
        assertThat(utbetalingsoppdrag.fagsystemId("prod")).isEqualTo("saksnummer")
    }

    @Test
    internal fun `skal ikke returnere 0 i prefix hvis den savner profile`() {
        val utbetalingsoppdrag = utbetalingsoppdrag
        assertThat(utbetalingsoppdrag.fagsystemId("")).isEqualTo("saksnummer")
    }
}