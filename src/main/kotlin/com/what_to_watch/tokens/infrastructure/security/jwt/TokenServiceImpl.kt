package com.what_to_watch.tokens.infrastructure.security.jwt

import com.what_to_watch.configs.JwtConfig
import com.what_to_watch.exceptions.auth.TokenInvalidException
import com.what_to_watch.exceptions.auth.TokenUnauthorizedException
import com.what_to_watch.tokens.domain.model.TokenPair
import com.what_to_watch.tokens.domain.service.TokenService
import com.what_to_watch.user.domain.model.User
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Clock
import java.time.Instant
import java.util.Date
import java.util.UUID
import java.util.function.Function

@Component
class TokenServiceImpl(
    private val publicKey: PublicKey,
    private val privateKey: PrivateKey,
    private val jwtConfig: JwtConfig,
    private val clock: Clock
) : TokenService {

    private val logger = KotlinLogging.logger {}

    override fun generateTokenPair(user: User): TokenPair {
        val tokenPairId = UUID.randomUUID().toString()

        val accessToken = generateAccessToken(user, tokenPairId)
        val refreshToken = generateRefreshToken(user, tokenPairId)

        return TokenPair(accessToken, refreshToken, tokenPairId)
    }

    override fun isTokenValid(token: String, user: User): Boolean {
        val now = Instant.now(clock)
        try {
            val claims = validateAndGetClaims(token)

            val tokenId = claims.subject
            if (user.id.toString() != tokenId) {
                return false
            }

            val tokenType = claims.get(jwtConfig.tokenTypeClaim, String::class.java)
            if (jwtConfig.accessTokenType != tokenType) {
                return false
            }

            return !claims.expiration.before(Date(now.toEpochMilli()))
        } catch (e: Exception) {
            logger.error { "Token validation failed: ${e.message}" }
            return false
        }
    }

    override fun extractTokenPairId(token: String, ignoreExpiration: Boolean): String? {
        return try {
            extractClaim(
                token = token,
                ignoreExpiration = ignoreExpiration,
                claimsResolver = { claims ->
                    claims?.get(jwtConfig.tokenPairIdClaim) as? String
                }
            )
        } catch (_: Exception) {
            null
        }
    }

    override fun extractId(token: String, ignoreExpiration: Boolean): UUID {
        try {
            return UUID.fromString(validateAndGetClaims(token, ignoreExpiration).subject)
        } catch (_: Exception) {
            throw TokenInvalidException()
        }
    }

    override fun isRefreshTokenValid(token: String, user: User): Boolean {
        val now = Instant.now(clock)
        try {
            val claims = validateAndGetClaims(token)

            val tokenType = claims.get(jwtConfig.tokenTypeClaim, String::class.java)
            if (jwtConfig.refreshTokenType != tokenType) {
                return false
            }

            val tokenId = claims.subject
            return !(user.id.toString() != tokenId || claims.expiration.before(Date(now.toEpochMilli())))
        } catch (e: Exception) {
            logger.error { "Refresh token validation failed: ${e.message}" }
            return false
        }
    }

    override fun isAccessTokenExpired(token: String): Boolean {
        val now = Instant.now(clock)
        try {
            val claims: Claims = Jwts
                .parser()
                .verifyWith(publicKey)
                .clock { Date(now.toEpochMilli()) }
                .build()
                .parseSignedClaims(token)
                .payload
            return claims.expiration.before(Date(now.toEpochMilli()))
        } catch (_: ExpiredJwtException) {
            return true
        } catch (e: JwtException) {
            logger.error { "Invalid token during expiration check: ${e.message}" }
            return true
        }
    }

    private fun <T> extractClaim(
        token: String?,
        claimsResolver: Function<Claims?, T?>,
        ignoreExpiration: Boolean = false
    ): T? {
        val claims: Claims = validateAndGetClaims(token, ignoreExpiration)
        return claimsResolver.apply(claims)
    }

    private fun generateAccessToken(user: User, tokenPairId: String): String {
        val claims: MutableMap<String, Any?> = HashMap()
        claims[jwtConfig.tokenPairIdClaim] = tokenPairId
        claims[jwtConfig.tokenTypeClaim] = jwtConfig.accessTokenType

        return generateToken(claims, user, jwtConfig.accessTokenExpiration)
    }

    private fun generateRefreshToken(user: User, tokenPairId: String): String {
        val claims: MutableMap<String, Any?> = HashMap()
        claims[jwtConfig.tokenPairIdClaim] = tokenPairId
        claims[jwtConfig.tokenTypeClaim] = jwtConfig.refreshTokenType

        return generateToken(claims, user, jwtConfig.refreshTokenExpiration)
    }

    private fun generateToken(
        extraClaims: MutableMap<String, Any?>,
        user: User,
        expiration: Long
    ): String {
        val now = Instant.now(clock)
        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(user.id.toString())
            .issuedAt(Date(now.toEpochMilli()))
            // Значения expiration указываются в секундах
            .expiration(Date(now.toEpochMilli() + expiration * 1000))
            .signWith(privateKey)
            .compact()
    }

    private fun validateAndGetClaims(token: String?, ignoreExpiration: Boolean = false): Claims {
        val now = Instant.now(clock)
        try {
            return Jwts
                .parser()
                .verifyWith(publicKey)
                .clock { Date(now.toEpochMilli()) }
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            if (ignoreExpiration) return e.claims
            throw TokenUnauthorizedException("Токен устарел")
        } catch (e: JwtException) {
            logger.error { "Failed to validate token: ${e.message}" }
            throw TokenUnauthorizedException("Токен невалиден")
        }
    }

}
