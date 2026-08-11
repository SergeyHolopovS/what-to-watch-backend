package com.what_to_watch.user

import com.what_to_watch.configs.CookieConfig
import com.what_to_watch.configs.FrontendConfig
import com.what_to_watch.configs.JwtConfig
import com.what_to_watch.exceptions.user.DiscordAuthException
import com.what_to_watch.tokens.domain.model.RefreshToken
import com.what_to_watch.tokens.domain.model.TokenPair
import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import com.what_to_watch.tokens.domain.service.TokenService
import com.what_to_watch.tokens.infrastructure.web.cookies.TokenCookieService
import com.what_to_watch.user.application.usecase.discordauth.AuthenticateViaDiscordUseCase
import com.what_to_watch.user.domain.gateway.DiscordOAuthGateway
import com.what_to_watch.user.domain.model.DiscordAccount
import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.domain.repository.UserRepository
import com.what_to_watch.user.domain.service.AccessWhitelist
import com.what_to_watch.user.infrastructure.web.AuthController
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpSession
import java.net.URI
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AuthControllerRedirectTest {

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

    private val cookieConfig = CookieConfig(
        accessTokenName = "access_token",
        refreshTokenName = "refresh_token",
        secure = false,
        sameSite = "Lax",
        refreshPath = "/refresh",
    )

    private val frontendConfig = FrontendConfig(redirectUri = "http://localhost:3000")

    private fun controller(gateway: DiscordOAuthGateway, useCase: AuthenticateViaDiscordUseCase) =
        AuthController(
            discordOAuthGateway = gateway,
            authenticateViaDiscordUseCase = useCase,
            tokenCookieService = TokenCookieService(jwtConfig, cookieConfig),
            frontendConfig = frontendConfig,
        )

    private fun fakeUseCase(gateway: DiscordOAuthGateway) = AuthenticateViaDiscordUseCase(
        userRepository = object : UserRepository {
            override fun addUser(name: String, passwordHash: String?, discordId: Long, avatarUrl: String) =
                User(UUID.randomUUID(), name, passwordHash, discordId, avatarUrl)
            override fun saveUser(user: User) = user
            override fun getUser(id: UUID): User? = null
            override fun getUserByDiscordId(discordId: Long): User? = null
            override fun getAllUsers(): List<User> = emptyList()
            override fun deleteUser(id: UUID) = Unit
            override fun existsByName(name: String) = false
        },
        discordOAuthGateway = gateway,
        accessWhitelist = object : AccessWhitelist {
            override fun isAllowed(discordId: Long) = true
        },
        tokenService = object : TokenService {
            override fun generateTokenPair(user: User) = TokenPair("access-value", "refresh-value", "pair-id")
            override fun isTokenValid(token: String, user: User) = true
            override fun extractTokenPairId(token: String, ignoreExpiration: Boolean) = "pair-id"
            override fun extractId(token: String, ignoreExpiration: Boolean): UUID = UUID.randomUUID()
            override fun isRefreshTokenValid(token: String, user: User) = true
            override fun isAccessTokenExpired(token: String) = true
        },
        refreshTokenRepository = object : RefreshTokenRepository {
            override fun findById(id: UUID) = throw UnsupportedOperationException()
            override fun findByToken(token: String) = throw UnsupportedOperationException()
            override fun isRefreshTokenActiveByTokenPairId(tokenPairId: String) = true
            override fun isRefreshTokenActiveInDatabase(token: String) = true
            override fun deleteById(id: UUID) = Unit
            override fun save(token: String, user: User, tokenPairId: String) = RefreshToken(
                UUID.randomUUID(), token, user, tokenPairId, true, Instant.now(), Instant.now(),
            )
            override fun deactivateToken(token: String) = Unit
            override fun deleteExpiredTokens() = Unit
        },
    )

    private fun fakeGateway(discordId: Long = 42L) = object : DiscordOAuthGateway {
        override fun buildAuthorizationUrl(state: String) = "https://discord.com/oauth2/authorize?state=$state"
        override fun exchangeCodeForAccount(code: String) =
            DiscordAccount(discordId, "nick", "https://cdn.discordapp.com/embed/avatars/0.png")
    }

    @Test
    fun `после успешного колбэка редиректит на frontend redirect-uri с cookie`() {
        val gateway = fakeGateway()
        val controller = controller(gateway, fakeUseCase(gateway))
        val session = MockHttpSession()

        val state = URI(controller.authorize(session).headers.location.toString()).query
            .substringAfter("state=")

        val response = controller.callback(code = "some-code", state = state, session = session)

        assertEquals(HttpStatus.FOUND, response.statusCode)
        assertEquals(URI.create("http://localhost:3000"), response.headers.location)

        val setCookies = response.headers["Set-Cookie"].orEmpty()
        assertTrue(setCookies.any { it.startsWith("access_token=access-value") })
        assertTrue(setCookies.any { it.startsWith("refresh_token=refresh-value") })
    }

    @Test
    fun `некорректный state отклоняется до обращения к Discord`() {
        val gateway = fakeGateway()
        val controller = controller(gateway, fakeUseCase(gateway))
        val session = MockHttpSession()
        controller.authorize(session)

        assertFailsWith<DiscordAuthException> {
            controller.callback(code = "some-code", state = "чужой-state", session = session)
        }
    }

}
