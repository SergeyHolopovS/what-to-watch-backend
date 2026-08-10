package com.what_to_watch.tokens

import com.what_to_watch.configs.JwtConfig
import com.what_to_watch.configs.JwtKeyConfig
import com.what_to_watch.tokens.infrastructure.security.jwt.TokenServiceImpl
import com.what_to_watch.user.domain.model.User
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TokenServiceImplTest {

    private val jwtConfig = JwtConfig(
        privateKey = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgv/p1NpJmKYDLnfnVfBKyx/D4bBN5" +
            "WxPooVAZALiU+rGhRANCAATaNtTOuwKnJxGkOhhnMPfgeb1NtvrrBd8hTT+v0lEkBwrfqo0YXyAEstGYvmAsxCfr" +
            "MJy+lhr8NBIeUOyHVXr0",
        publicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE2jbUzrsCpycRpDoYZzD34Hm9Tbb66wXfIU0/r9JRJAcK" +
            "36qNGF8gBLLRmL5gLMQn6zCcvpYa/DQSHlDsh1V69A==",
        accessTokenExpiration = 900,
        refreshTokenExpiration = 864000,
        tokenPairIdClaim = "tokenPairId",
        tokenTypeClaim = "tokenType",
        accessTokenType = "access",
        refreshTokenType = "refresh",
    )

    private val keyConfig = JwtKeyConfig(jwtConfig)

    private val user = User(
        id = UUID.randomUUID(),
        name = "tester",
        passwordHash = null,
        discordId = 123456789L,
        avatarUrl = "https://cdn.discordapp.com/embed/avatars/0.png",
    )

    private fun tokenService(clock: Clock) = TokenServiceImpl(
        publicKey = keyConfig.publicKey(),
        privateKey = keyConfig.privateKey(),
        jwtConfig = jwtConfig,
        clock = clock,
    )

    @Test
    fun `создаёт валидную пару токенов с общим tokenPairId`() {
        val service = tokenService(Clock.systemUTC())

        val pair = service.generateTokenPair(user)

        assertTrue(service.isTokenValid(pair.accessToken, user))
        assertTrue(service.isRefreshTokenValid(pair.refreshToken, user))
        assertFalse(service.isAccessTokenExpired(pair.accessToken))
        assertEquals(pair.tokenPairId, service.extractTokenPairId(pair.accessToken))
        assertEquals(pair.tokenPairId, service.extractTokenPairId(pair.refreshToken))
        assertEquals(user.id, service.extractId(pair.accessToken))
    }

    @Test
    fun `access токен не проходит проверку как refresh и наоборот`() {
        val service = tokenService(Clock.systemUTC())

        val pair = service.generateTokenPair(user)

        assertFalse(service.isRefreshTokenValid(pair.accessToken, user))
        assertFalse(service.isTokenValid(pair.refreshToken, user))
    }

    @Test
    fun `токен другого пользователя невалиден`() {
        val service = tokenService(Clock.systemUTC())

        val pair = service.generateTokenPair(user)
        val other = user.copy(id = UUID.randomUUID())

        assertFalse(service.isTokenValid(pair.accessToken, other))
        assertFalse(service.isRefreshTokenValid(pair.refreshToken, other))
    }

    @Test
    fun `просроченный access токен распознаётся, а id всё ещё извлекается`() {
        val issuedAt = Instant.now()
        val pair = tokenService(Clock.fixed(issuedAt, ZoneOffset.UTC)).generateTokenPair(user)

        val later = Clock.fixed(
            issuedAt.plus(Duration.ofSeconds(jwtConfig.accessTokenExpiration + 60)),
            ZoneOffset.UTC,
        )
        val service = tokenService(later)

        assertTrue(service.isAccessTokenExpired(pair.accessToken))
        assertFalse(service.isTokenValid(pair.accessToken, user))
        // refresh живёт дольше и остаётся валидным
        assertTrue(service.isRefreshTokenValid(pair.refreshToken, user))
        // ignoreExpiration позволяет достать данные из протухшего access токена
        assertEquals(user.id, service.extractId(pair.accessToken, true))
        assertEquals(pair.tokenPairId, service.extractTokenPairId(pair.accessToken, true))
    }

}
