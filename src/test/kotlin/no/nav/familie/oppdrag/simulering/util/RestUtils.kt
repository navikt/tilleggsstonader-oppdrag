package no.nav.familie.oppdrag.rest

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.security.token.support.test.JwkGenerator
import no.nav.security.token.support.test.JwtTokenGenerator
import org.springframework.http.HttpHeaders
import java.util.*

fun HttpHeaders.withToken(accessAsApplication: Boolean = true): HttpHeaders {
    val createSignedJWT = token(accessAsApplication)
    this.setBearerAuth(createSignedJWT.serialize())
    return this
}

private fun token(accessAsApplication: Boolean): SignedJWT {
    val thisId = UUID.randomUUID().toString()
    val clientId = UUID.randomUUID().toString()
    var claimsSet = JwtTokenGenerator.createSignedJWT(clientId).jwtClaimsSet
    val builder = JWTClaimsSet.Builder(claimsSet)
            .claim("oid", thisId)
            .claim("sub", thisId)
            .claim("azp", clientId)

    if (accessAsApplication) {
        builder.claim("roles", listOf("access_as_application"))
    }

    claimsSet = builder.build()
    return JwtTokenGenerator.createSignedJWT(JwkGenerator.getDefaultRSAKey(), claimsSet)
}