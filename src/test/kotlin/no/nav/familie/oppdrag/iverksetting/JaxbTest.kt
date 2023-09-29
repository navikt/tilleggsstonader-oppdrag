package no.nav.familie.oppdrag.iverksetting

import no.nav.familie.kontrakter.felles.objectMapper
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse
import no.nav.system.os.entiteter.beregningskjema.Beregning
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsvedtakDto
import no.trygdeetaten.skjema.oppdrag.Oppdrag
import no.trygdeetaten.skjema.oppdrag.Oppdrag110
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.util.GregorianCalendar
import javax.xml.datatype.DatatypeFactory
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest as SimulerBeregningRequest1
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningResponse as SimulerBeregningResponse1

internal class JaxbTest {

    @Test
    internal fun `oppdrag tilXml`() {
        val dato = LocalDate.of(2022, 1, 1)
        val xmlGregorianCalendar = DatatypeFactory.newInstance()
            .newXMLGregorianCalendar(GregorianCalendar.from(dato.atStartOfDay(ZoneId.of("Europe/Oslo"))))
        val oppdrag = Oppdrag().apply {
            oppdrag110 = Oppdrag110().apply {
                datoForfall = xmlGregorianCalendar
            }
        }
        val xml = Jaxb.tilXml(oppdrag)
        val obj = Jaxb.tilOppdrag(xml)
        assertThat(xml).isEqualTo(Jaxb.tilXml(obj))
        assertThat(xml).isEqualTo(loadResource("xml/Oppdrag.xml"))
    }

    @Test
    internal fun `SimulerBeregningRequest tilXml`() {
        val request = SimulerBeregningRequest().apply {
            request = SimulerBeregningRequest1().apply {
                simuleringsPeriode = SimulerBeregningRequest1.SimuleringsPeriode().apply {
                    datoSimulerFom = LocalDate.of(2022, 1, 2).toString()
                }
            }
        }
        val xml = Jaxb.tilXml(request)
        assertThat(xml).isEqualTo(loadResource("xml/SimulerBeregningRequest.xml"))
    }

    @Test
    internal fun `SimulerBeregningResponse tilXml`() {
        val response = SimulerBeregningResponse().apply {
            response = SimulerBeregningResponse1().apply {
                simulering = Beregning().apply {
                    belop = BigDecimal.TEN
                    datoBeregnet = LocalDate.of(2022, 1, 2).toString()
                }
            }
        }
        val xml = Jaxb.tilXml(response)
        val obj = Jaxb.tilSimuleringsrespons(xml)
        assertThat(xml).isEqualTo(Jaxb.tilXml(obj))
        assertThat(xml).isEqualTo(loadResource("xml/SimulerBeregningResponse.xml"))
    }

    @Test
    internal fun `iverksetting av tilbakekreving skal returnere localdate og ikke xmlgregorian`() {
        val response = TilbakekrevingsvedtakResponse().apply {
            tilbakekrevingsvedtak = TilbakekrevingsvedtakDto().apply {
                datoVedtakFagsystem = LocalDate.of(2022, 1, 1)
            }
        }
        assertThat(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)).isEqualTo(
            """
            {
              "mmel" : null,
              "tilbakekrevingsvedtak" : {
                "kodeAksjon" : null,
                "vedtakId" : null,
                "datoVedtakFagsystem" : "2022-01-01",
                "kodeHjemmel" : null,
                "renterBeregnes" : null,
                "enhetAnsvarlig" : null,
                "kontrollfelt" : null,
                "saksbehId" : null,
                "tilbakekrevingsperiode" : null
              }
            }
            """.trimIndent(),
        )
    }

    private fun loadResource(file: String) = JaxbTest::class.java.classLoader.getResource(file)!!.readText()
}
