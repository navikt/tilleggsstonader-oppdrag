package no.nav.familie.oppdrag.simulering.repository

import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

data class SimuleringMottaker (val id: Long? = null,
                               @Column("simulering_id") val simuleringGrunnlag: SimuleringGrunnlag,
                               val mottakerNummer: String? = null,
                               @Column("mottaker_type") val mottakerType: MottakerType,
                               @Column("opprettet_tidspunkt") val opprettetTidspunkt: LocalDateTime = LocalDateTime.now(),
                                ) {

    /*fun getSimulertePosteringer(): List<SimulertPostering> {
        return simulertePosteringer.stream().filter { sp: SimulertPostering -> !sp.erUtenInntrekk() }
            .collect(Collectors.toList<Any>())
    }

    val simulertePosteringerUtenInntrekk: List<SimulertPostering>
        get() = simulertePosteringer.stream()
            .filter(no.nav.foreldrepenger.oppdrag.oppdragslager.simulering.SimulertPostering::erUtenInntrekk)
            .collect(Collectors.toList<Any>())

    // Skal ikke kunne kombinere tilbakekreving og inntrekk, bruker derfor uten inntrekk hvis det finnes
    val simulertePosteringerForFeilutbetaling: List<SimulertPostering>
        get() {
            // Skal ikke kunne kombinere tilbakekreving og inntrekk, bruker derfor uten inntrekk hvis det finnes
            val simulertePosteringerUtenInntrekk =
                simulertePosteringerUtenInntrekk
            return if (simulertePosteringerUtenInntrekk.isEmpty()) getSimulertePosteringer() else simulertePosteringerUtenInntrekk
        }

    val alleSimulertePosteringer: List<SimulertPostering>
        get() = simulertePosteringer

    fun leggTilSimulertPostering(simulertPostering: SimulertPostering) {
        simulertPostering.simuleringMottaker = this
        simulertePosteringer.add(simulertPostering)
    }*/

    override fun toString(): String {
        return (javaClass.simpleName + "<id=" + id
                + ", simuleringGrunnlag=" + simuleringGrunnlag!!.id
                + ", mottakerNummer=" + mottakerNummer
                + ", mottakerType=" + mottakerType
                + ">")
    }
}
