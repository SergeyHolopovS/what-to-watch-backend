package com.what_to_watch.user.application.usecase.discordauth

import com.what_to_watch.exceptions.user.DiscordNotWhitelistedException
import com.what_to_watch.tokens.domain.model.TokenPair
import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import com.what_to_watch.tokens.domain.service.TokenService
import com.what_to_watch.user.domain.gateway.DiscordOAuthGateway
import com.what_to_watch.user.domain.model.DiscordAccount
import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.domain.repository.UserRepository
import com.what_to_watch.user.domain.service.AccessWhitelist
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class AuthenticateViaDiscordUseCase (
    val userRepository: UserRepository,
    val discordOAuthGateway: DiscordOAuthGateway,
    val accessWhitelist: AccessWhitelist,
    val tokenService: TokenService,
    val refreshTokenRepository: RefreshTokenRepository,
) {

    private val logger = KotlinLogging.logger {}

    fun execute(command: AuthenticateViaDiscordCommand): DiscordAuthResult {
        // Обмениваем код авторизации на данные аккаунта Discord
        val account = discordOAuthGateway.exchangeCodeForAccount(command.code)

        // Пускаем дальше только discordId из вайтлиста: ни аккаунта, ни токенов
        // для постороннего не создаётся
        if (!accessWhitelist.isAllowed(account.discordId)) {
            logger.warn { "Попытка входа с discordId ${account.discordId} вне вайтлиста" }
            throw DiscordNotWhitelistedException()
        }

        // Ищем пользователя по discordId, при первом входе — создаём
        val existingUser: User? = userRepository.getUserByDiscordId(account.discordId)
        val user: User = existingUser
            ?.takeIf { it.avatarUrl == account.avatarUrl }
            ?: existingUser
                // Аватар в Discord мог смениться со времени прошлого входа — обновляем
                ?.let { userRepository.saveUser(it.copy(avatarUrl = account.avatarUrl)) }
            ?: userRepository.addUser(
                name = pickFreeName(account),
                passwordHash = null,
                discordId = account.discordId,
                avatarUrl = account.avatarUrl,
            )

        // Создаём пару токенов
        val tokens: TokenPair = tokenService.generateTokenPair(user)

        // Сохраняем сессию refresh токена в бд
        refreshTokenRepository.save(
            tokens.refreshToken,
            user,
            tokens.tokenPairId
        )

        // Возвращаем токены
        return DiscordAuthResult(
            user = user,
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
        )
    }

    /**
     * Имя пользователя уникально, а ник в Discord мог быть уже занят локальной
     * регистрацией — в этом случае дополняем его discordId.
     */
    private fun pickFreeName(account: DiscordAccount): String =
        if (userRepository.existsByName(account.username)) {
            "${account.username}#${account.discordId}"
        } else {
            account.username
        }

}
