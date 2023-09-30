package no.nav.tilleggsstonader.oppdrag.avstemming.grensesnitt

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Metrics
import no.nav.familie.kontrakter.felles.objectMapper
import no.nav.familie.kontrakter.felles.oppdrag.GrensesnittavstemmingRequest
import no.nav.tilleggsstonader.oppdrag.avstemming.AvstemmingSender
import no.nav.tilleggsstonader.oppdrag.repository.OppdragLagerRepository
import no.nav.virksomhet.tjenester.avstemming.meldinger.v1.Grunnlagsdata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GrensesnittavstemmingService(
    private val avstemmingSender: AvstemmingSender,
    private val oppdragLagerRepository: OppdragLagerRepository,
    @Value("\${grensesnitt.antall:7000}") private val antall: Int,
) {

    private val secureLogger = LoggerFactory.getLogger("secureLogger")
    private var countere: MutableMap<String, Map<String, Counter>> = HashMap()

    init {
        enumValues<Fagsystem>().forEach {
            countere[it.name] = opprettMetrikkerForFagsystem(it)
        }
    }

    fun utførGrensesnittavstemming(request: GrensesnittavstemmingRequest) {
        val (fagsystem: String, fra: LocalDateTime, til: LocalDateTime) = request
        var page = 0
        var antallOppdragSomSkalAvstemmes = 0
        var oppdragSomSkalAvstemmes =
            oppdragLagerRepository.hentIverksettingerForGrensesnittavstemming(fra, til, fagsystem, antall, page++)
        if (oppdragSomSkalAvstemmes.isEmpty()) {
            LOG.info("Ingen oppdrag å gjennomføre grensesnittavstemming for.")
            return
        }
        val avstemmingMapper = GrensesnittavstemmingMapper(fagsystem, fra, til)
        LOG.info("Utfører grensesnittavstemming for id: ${avstemmingMapper.avstemmingId}")
        avstemmingSender.sendGrensesnittAvstemming(avstemmingMapper.lagStartmelding())
        while (oppdragSomSkalAvstemmes.isNotEmpty()) {
            val meldinger = avstemmingMapper.lagAvstemmingsmeldinger(oppdragSomSkalAvstemmes)
            meldinger.forEach { avstemmingSender.sendGrensesnittAvstemming(it) }

            antallOppdragSomSkalAvstemmes += oppdragSomSkalAvstemmes.size
            oppdragSomSkalAvstemmes =
                oppdragLagerRepository.hentIverksettingerForGrensesnittavstemming(fra, til, fagsystem, antall, page++)
        }
        val totalmelding = avstemmingMapper.lagTotalMelding()
        avstemmingSender.sendGrensesnittAvstemming(totalmelding)
        avstemmingSender.sendGrensesnittAvstemming(avstemmingMapper.lagSluttmelding())

        LOG.info(
            "Fullført grensesnittavstemming for id: ${avstemmingMapper.avstemmingId}" +
                " antallOppdragSomSkalAvstemmes=$antallOppdragSomSkalAvstemmes",
        )
        secureLogger.info(
            "Fullført grensesnittavstemming for id: ${avstemmingMapper.avstemmingId} " +
                "totalmelding=${objectMapper.writeValueAsString(totalmelding)}",
        )

        oppdaterMetrikker(fagsystem, totalmelding.grunnlag)
    }

    private fun oppdaterMetrikker(fagsystem: String, grunnlag: Grunnlagsdata) {
        val metrikkerForFagsystem = countere.getValue(fagsystem)

        metrikkerForFagsystem.getValue(Status.GODKJENT.status).increment(grunnlag.godkjentAntall.toDouble())
        metrikkerForFagsystem.getValue(Status.AVVIST.status).increment(grunnlag.avvistAntall.toDouble())
        metrikkerForFagsystem.getValue(Status.MANGLER.status).increment(grunnlag.manglerAntall.toDouble())
        metrikkerForFagsystem.getValue(Status.VARSEL.status).increment(grunnlag.varselAntall.toDouble())
    }

    private fun opprettMetrikkerForFagsystem(fagsystem: Fagsystem): Map<String, Counter> {
        val PACKAGE_NAME = "familie.oppdrag.grensesnittavstemming"
        val godkjentCounter = Metrics.counter(
            PACKAGE_NAME,
            "fagsystem",
            fagsystem.name,
            "status",
            Status.GODKJENT.status,
            "beskrivelse",
            Status.GODKJENT.beskrivelse,
        )
        val avvistCounter = Metrics.counter(
            PACKAGE_NAME,
            "fagsystem",
            fagsystem.name,
            "status",
            Status.AVVIST.status,
            "beskrivelse",
            Status.AVVIST.beskrivelse,
        )
        val manglerCounter = Metrics.counter(
            PACKAGE_NAME,
            "fagsystem",
            fagsystem.name,
            "status",
            Status.MANGLER.status,
            "beskrivelse",
            Status.MANGLER.beskrivelse,
        )
        val varselCounter = Metrics.counter(
            PACKAGE_NAME,
            "fagsystem",
            fagsystem.name,
            "status",
            Status.VARSEL.status,
            "beskrivelse",
            Status.VARSEL.beskrivelse,
        )

        return hashMapOf(
            Status.GODKJENT.status to godkjentCounter,
            Status.AVVIST.status to avvistCounter,
            Status.MANGLER.status to manglerCounter,
            Status.VARSEL.status to varselCounter,
        )
    }

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(GrensesnittavstemmingService::class.java)
    }
}

enum class Status(val status: String, val beskrivelse: String) {
    GODKJENT("godkjent", "Antall oppdrag som har fått OK kvittering (alvorlighetsgrad 00)."),
    AVVIST(
        "avvist",
        "Antall oppdrag som har fått kvittering med funksjonell eller teknisk feil, samt ukjent (alvorlighetsgrad 08 og 12).",
    ),
    MANGLER("mangler", "Antall oppdrag hvor kvittering mangler."),
    VARSEL("varsel", "Antall oppdrag som har fått kvittering med mangler (alvorlighetsgrad 04)."),
}

enum class Fagsystem {
    BA,
    EFOG,
    EFBT,
    EFSP,
    KS,
}
