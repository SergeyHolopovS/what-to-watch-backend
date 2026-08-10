package com.what_to_watch.tokens.application.usecase.refresh

import com.what_to_watch.exceptions.auth.AccessStillActiveException
import com.what_to_watch.exceptions.auth.RefreshTokenInvalidException
import com.what_to_watch.exceptions.auth.RefreshTokenNotActiveException
import com.what_to_watch.exceptions.auth.TokenPairIdMismatchException
import com.what_to_watch.exceptions.auth.TokensIdMismatchException
import com.what_to_watch.exceptions.user.UserNotFoundException
import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import com.what_to_watch.tokens.domain.service.TokenService
import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.domain.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class RefreshUseCase(
    private val tokenService: TokenService,
    private val repository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {

    private val logger = KotlinLogging.logger {}

    fun execute(command: RefreshCommand): RefreshResult {
        val userId = tokenService.extractId(command.refreshToken, false)
        val user: User = userRepository.getUser(userId) ?: throw UserNotFoundException(userId)

        if (!tokenService.isRefreshTokenValid(command.refreshToken, user)) throw RefreshTokenInvalidException()

        if (!tokenService.isAccessTokenExpired(command.accessToken)) throw AccessStillActiveException()

        val accessTokenId = tokenService.extractId(command.accessToken, true)
        if (user.id != accessTokenId) throw TokensIdMismatchException()

        val accessTokenPairId = tokenService.extractTokenPairId(command.accessToken, true)
        val refreshTokenPairId = tokenService.extractTokenPairId(command.refreshToken)
        if (
            accessTokenPairId == null ||
            refreshTokenPairId == null ||
            accessTokenPairId != refreshTokenPairId
        ) {
            logger.warn { "Token pair mismatch for user with id: ${user.id}" }
            throw TokenPairIdMismatchException()
        }

        if (!repository.isRefreshTokenActiveInDatabase(command.refreshToken)) throw RefreshTokenNotActiveException()

        // Гасим именно предъявленный refresh токен: иначе старая пара осталась бы рабочей
        repository.deactivateToken(command.refreshToken)

        val tokenPair = tokenService.generateTokenPair(user)

        repository.save(
            tokenPairId = tokenPair.tokenPairId,
            token = tokenPair.refreshToken,
            user = user
        )

        return RefreshResult(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
        )
    }

}
