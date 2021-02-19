package no.nav.familie.oppdrag.rest

import no.nav.familie.oppdrag.util.TestConfig
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/authTest")
@ProtectedWithClaims(issuer = "azuread")
class AuthController {

    @GetMapping
    fun authCheck() = "ok"
}

@ActiveProfiles("integrasjonstest")
@SpringBootTest(classes = [TestConfig::class],
                properties = ["spring.cloud.vault.enabled=false"],
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    private val restTemplate = TestRestTemplate()

    @Test
    fun `uten token`() {
        val result = kallPing(HttpHeaders())
        assertThat(result.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `med token - uten client credentials`() {
        val result = kallPing(HttpHeaders().withToken(accessAsApplication = false))
        assertThat(result.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `med token - med client credentials`() {
        val result = kallPing(HttpHeaders().withToken(accessAsApplication = true))
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

    private fun kallPing(headers: HttpHeaders): ResponseEntity<String> {
        return restTemplate.exchange("http://localhost:$port/api/authTest", HttpMethod.GET, HttpEntity<Any>(headers) )
    }

}