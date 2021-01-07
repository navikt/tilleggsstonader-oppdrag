package no.nav.familie.oppdrag.simulering.mock

import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate

class Periode : Comparable<Periode> {
    var fom: LocalDate
        private set
    var tom: LocalDate
    var sats: BigDecimal? = null
        private set
    var oldSats: BigDecimal? = null
        private set
    var periodeType: PeriodeType? = null
    var kodeKlassifik: String? = null
        private set

    internal constructor(fom: LocalDate, tom: LocalDate) {
        this.fom = fom
        this.tom = tom
    }

    internal constructor(fom: LocalDate, tom: LocalDate, sats: BigDecimal?, kodeKlassifik: String?) {
        this.fom = fom
        this.tom = tom
        this.sats = sats
        this.kodeKlassifik = kodeKlassifik
    }

    internal constructor(
        fom: LocalDate,
        tom: LocalDate,
        sats: BigDecimal?,
        kodeKlassifik: String?,
        periodeType: PeriodeType?
    ) {
        this.fom = fom
        this.tom = tom
        this.sats = sats
        this.kodeKlassifik = kodeKlassifik
        this.periodeType = periodeType
    }

    internal constructor(
        fom: LocalDate,
        tom: LocalDate,
        oldSats: BigDecimal,
        sats: BigDecimal?,
        kodeKlassifik: String?
    ) {
        this.fom = fom
        this.tom = tom
        this.oldSats = oldSats
        this.sats = sats
        this.kodeKlassifik = kodeKlassifik
        if (oldSats.compareTo(sats) <= 0) {
            periodeType = PeriodeType.ØKNING
        } else {
            periodeType = PeriodeType.REDUKSJON
        }
    }

    val feilSats: BigDecimal
        get() = if (periodeType != PeriodeType.OPPH) {
            BigDecimal.ZERO
        } else oldSats!!.subtract(sats)
    val antallVirkedager: Int
        get() {
            var startDato = fom
            var antallVirkedager = 0
            while (startDato.isBefore(tom) || startDato.isEqual(tom)) {
                if (startDato.dayOfWeek != DayOfWeek.SATURDAY && startDato.dayOfWeek != DayOfWeek.SUNDAY) {
                    antallVirkedager++
                }
                startDato = startDato.plusDays(1)
            }
            return antallVirkedager
        }

    override fun compareTo(comparePeriode: Periode): Int {
        return fom.compareTo(comparePeriode.fom)
    }
}
