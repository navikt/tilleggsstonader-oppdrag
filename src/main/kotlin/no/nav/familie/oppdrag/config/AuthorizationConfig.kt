package no.nav.familie.oppdrag.config

import no.nav.familie.sikkerhet.TokenValidationFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthorizationConfig {

    @Bean
    fun logFilter(): FilterRegistrationBean<TokenValidationFilter> {
        val filterRegistration = FilterRegistrationBean<TokenValidationFilter>()
        filterRegistration.filter = TokenValidationFilter(acceptClientCredential = true)
        filterRegistration.order = 0
        return filterRegistration
    }
}
