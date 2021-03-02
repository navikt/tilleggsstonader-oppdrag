package no.nav.familie.oppdrag.config

import no.nav.familie.sikkerhet.ClientTokenValidationFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthorizationConfig {

    @Bean
    fun clientTokenValidationFilter(): FilterRegistrationBean<ClientTokenValidationFilter> {
        val filterRegistration = FilterRegistrationBean<ClientTokenValidationFilter>()
        filterRegistration.filter = ClientTokenValidationFilter(acceptClientCredential = true)
        filterRegistration.order = 0
        return filterRegistration
    }
}
