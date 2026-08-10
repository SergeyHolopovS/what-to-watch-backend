package com.what_to_watch.user

import com.what_to_watch.configs.WhitelistConfig
import com.what_to_watch.exceptions.user.DiscordNotWhitelistedException
import com.what_to_watch.tokens.domain.model.RefreshToken
import com.what_to_watch.tokens.domain.model.TokenPair
import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import com.what_to_watch.tokens.domain.service.TokenService
import com.what_to_watch.user.application.usecase.discordauth.AuthenticateViaDiscordCommand
import com.what_to_watch.user.application.usecase.discordauth.AuthenticateViaDiscordUseCase
import com.what_to_watch.user.domain.gateway.DiscordOAuthGateway
import com.what_to_watch.user.domain.model.DiscordAccount
import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.domain.repository.UserRepository
import com.what_to_watch.user.infrastructure.security.DiscordAccessWhitelist
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DiscordWhitelistTest {

    private val allowedDiscordId = 111L
    private val strangerDiscordId = 999L

    private fun whitelist(raw: String) = DiscordAccessWhitelist(WhitelistConfig(raw))

    @Test
    fun `список парсится с пробелами и пустыми элементами`() {
        val config = WhitelistConfig(" 111 , 222,, 333 ,")

        assertEquals(setOf(111L, 222L, 333L), config.discordIds)
    }

    @Test
    fun `пустой список никого не пускает`() {
        val whitelist = whitelist("")

        assertFalse(whitelist.isAllowed(allowedDiscordId))
        assertFalse(whitelist.isAllowed(strangerDiscordId))
    }

    @Test
    fun `пускает только id из списка`() {
        val whitelist = whitelist("$allowedDiscordId")

        assertTrue(whitelist.isAllowed(allowedDiscordId))
        assertFalse(whitelist.isAllowed(strangerDiscordId))
    }

    @Test
    fun `аккаунт и токены не создаются для id вне вайтлиста`() {
        val users = FakeUserRepository()
        val tokens = FakeRefreshTokenRepository()
        val useCase = useCase(strangerDiscordId, users, tokens, whitelist("$allowedDiscordId"))

        assertFailsWith<DiscordNotWhitelistedException> {
            useCase.execute(AuthenticateViaDiscordCommand("code"))
        }

        assertTrue(users.added.isEmpty(), "пользователь не должен создаваться")
        assertTrue(tokens.saved.isEmpty(), "сессия refresh токена не должна создаваться")
    }

    @Test
    fun `для id из вайтлиста создаётся аккаунт и выдаются токены`() {
        val users = FakeUserRepository()
        val tokens = FakeRefreshTokenRepository()
        val useCase = useCase(allowedDiscordId, users, tokens, whitelist("$allowedDiscordId"))

        val result = useCase.execute(AuthenticateViaDiscordCommand("code"))

        assertEquals(1, users.added.size)
        assertEquals(allowedDiscordId, users.added.single().discordId)
        assertEquals(1, tokens.saved.size)
        assertEquals("access", result.accessToken)
        assertEquals("refresh", result.refreshToken)
    }

    private fun useCase(
        discordId: Long,
        users: FakeUserRepository,
        tokens: FakeRefreshTokenRepository,
        whitelist: DiscordAccessWhitelist,
    ) = AuthenticateViaDiscordUseCase(
        userRepository = users,
        discordOAuthGateway = object : DiscordOAuthGateway {
            override fun buildAuthorizationUrl(state: String) = "url"
            override fun exchangeCodeForAccount(code: String) =
                DiscordAccount(discordId, "nick", "https://cdn.discordapp.com/embed/avatars/0.png")
        },
        accessWhitelist = whitelist,
        tokenService = FakeTokenService(),
        refreshTokenRepository = tokens,
    )

    private class FakeUserRepository : UserRepository {
        val added = mutableListOf<User>()

        override fun addUser(name: String, passwordHash: String?, discordId: Long, avatarUrl: String): User =
            User(UUID.randomUUID(), name, passwordHash, discordId, avatarUrl).also { added += it }

        override fun saveUser(user: User): User = user
        override fun getUser(id: UUID): User? = null
        override fun getUserByDiscordId(discordId: Long): User? = null
        override fun getAllUsers(): List<User> = emptyList()
        override fun deleteUser(id: UUID) = Unit
        override fun existsByName(name: String): Boolean = false
    }

    private class FakeRefreshTokenRepository : RefreshTokenRepository {
        val saved = mutableListOf<String>()

        override fun save(token: String, user: User, tokenPairId: String): RefreshToken {
            saved += token
            return RefreshToken(
                id = UUID.randomUUID(),
                token = token,
                user = user,
                tokenPairId = tokenPairId,
                active = true,
                createdAt = Instant.now(),
                expiresAt = Instant.now(),
            )
        }

        override fun findById(id: UUID) = throw UnsupportedOperationException()
        override fun findByToken(token: String) = throw UnsupportedOperationException()
        override fun isRefreshTokenActiveByTokenPairId(tokenPairId: String) = true
        override fun isRefreshTokenActiveInDatabase(token: String) = true
        override fun deleteById(id: UUID) = Unit
        override fun deactivateToken(token: String) = Unit
        override fun deleteExpiredTokens() = Unit
    }

    private class FakeTokenService : TokenService {
        override fun generateTokenPair(user: User) = TokenPair("access", "refresh", "pair")
        override fun isTokenValid(token: String, user: User) = true
        override fun extractTokenPairId(token: String, ignoreExpiration: Boolean) = "pair"
        override fun extractId(token: String, ignoreExpiration: Boolean): UUID = UUID.randomUUID()
        override fun isRefreshTokenValid(token: String, user: User) = true
        override fun isAccessTokenExpired(token: String) = true
    }

}
