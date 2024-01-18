package com.example.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.AuthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*


val jwtAudience = System.getenv("jwt.audience")
val jwtIssuer = System.getenv("jwt.domain")
val jwtSecret = System.getenv("jwt.secret")

private const val CLAIM = "email"

fun Application.configureSecurity() {

    authentication {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                 if (credential.payload.getClaim(CLAIM).asString() != null){
                    JWTPrincipal(payload = credential.payload)
                 }else{
                     null
                 }
            }

            challenge{_, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(
                        errorMessage = "Token is not valid or expired "
                    )
                )
            }
        }
    }
}


fun generateToken(email: String): String{
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim(CLAIM, email)
        .sign(Algorithm.HMAC256(jwtSecret))

}
