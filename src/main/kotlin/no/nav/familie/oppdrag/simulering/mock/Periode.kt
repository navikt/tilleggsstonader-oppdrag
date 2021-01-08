package no.nav.familie.oppdrag.simulering.mock

import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate

data class Periode constructor(val fom: LocalDate,
                               var tom: LocalDate,
                               val sats: BigDecimal?,
                               val oldSats: BigDecimal?,
                               val typeSats: String?,
                               var periodeType: PeriodeType?,
                               val kodeKlassifik: String?): Comparable<Periode> {
//    var fom: LocalDate
//        private set
//    var tom: LocalDate
//    var sats: BigDecimal? = null
//        private set
//    var oldSats: BigDecimal? = null
//        private set
//    var typeSats: String? = null
//    var periodeType: PeriodeType? = null
//    var kodeKlassifik: String? = null
//        private set

//    internal constructor(fom: LocalDate, tom: LocalDate) {
//        this.fom = fom
//        this.tom = tom
//    }
//
//    internal constructor(fom: LocalDate, tom: LocalDate, sats: BigDecimal?, typeSats: String?, kodeKlassifik: String?) {
//        this.fom = fom
//        this.tom = tom
//        this.sats = sats
//        this.typeSats = typeSats
//        this.kodeKlassifik = kodeKlassifik
//    }

    constructor(
        fom: LocalDate,
        tom: LocalDate,
        sats: BigDecimal? = null,
        typeSats: String? = null,
        kodeKlassifik: String? = null,
        periodeType: PeriodeType? = null) :
            this(
                fom = fom,
                tom = tom,
                sats = sats,
                oldSats = null,
                typeSats = typeSats,
                kodeKlassifik = kodeKlassifik,
                periodeType = periodeType)

    constructor(
        fom: LocalDate,
        tom: LocalDate,
        oldSats: BigDecimal,
        sats: BigDecimal?,
        typeSats: String?,
        kodeKlassifik: String?) :
            this(
                fom = fom,
                tom = tom,
                oldSats = oldSats,
                sats = sats,
                typeSats = typeSats,
                kodeKlassifik = kodeKlassifik,
                periodeType = if (oldSats <= sats) {
                    PeriodeType.ØKNING
                } else {
                    PeriodeType.REDUKSJON
                }
            )

    override fun compareTo(other: Periode): Int {
        return fom.compareTo(other.fom)
    }

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
}
