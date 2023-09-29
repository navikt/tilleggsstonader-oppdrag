package no.nav.familie.oppdrag.config

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.familie.kontrakter.felles.objectMapper
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.trygdeetaten.skjema.oppdrag.Mmel
import org.postgresql.util.PGobject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class DatabaseConfiguration : AbstractJdbcConfiguration() {

    @Bean
    fun operations(dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }

    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return JdbcCustomConversions(
            listOf(
                PGobjectTilUtbetalingsoppdragConverter(),
                UtbetalingsoppdragTilPGobjectConverter(),
                PGobjectTilMmelConverter(),
                MmelTilPGobjectConverter(),
            ),
        )
    }

    @ReadingConverter
    class PGobjectTilUtbetalingsoppdragConverter : Converter<PGobject, Utbetalingsoppdrag> {

        override fun convert(pGobject: PGobject): Utbetalingsoppdrag? {
            return pGobject.value?.let { objectMapper.readValue(it) }
        }
    }

    @WritingConverter
    class UtbetalingsoppdragTilPGobjectConverter : Converter<Utbetalingsoppdrag, PGobject> {

        override fun convert(utbetalingsoppdrag: Utbetalingsoppdrag): PGobject =
            PGobject().apply {
                type = "json"
                value = objectMapper.writeValueAsString(utbetalingsoppdrag)
            }
    }

    @ReadingConverter
    class PGobjectTilMmelConverter : Converter<PGobject, Mmel> {

        override fun convert(pGobject: PGobject): Mmel? {
            return pGobject.value?.let { objectMapper.readValue(it) }
        }
    }

    @WritingConverter
    class MmelTilPGobjectConverter : Converter<Mmel, PGobject> {

        override fun convert(utbetalingsoppdrag: Mmel): PGobject =
            PGobject().apply {
                type = "json"
                value = objectMapper.writeValueAsString(utbetalingsoppdrag)
            }
    }
}
