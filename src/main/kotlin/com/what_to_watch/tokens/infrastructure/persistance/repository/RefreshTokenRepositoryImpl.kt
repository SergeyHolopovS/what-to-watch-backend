package com.what_to_watch.tokens.infrastructure.persistance.repository

import com.what_to_watch.configs.JwtConfig
import com.what_to_watch.exceptions.tokens.RefreshTokenNotFoundException
import com.what_to_watch.tokens.domain.model.RefreshToken
import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import com.what_to_watch.tokens.infrastructure.mappers.RefreshTokenMapper
import com.what_to_watch.tokens.infrastructure.persistance.entity.RefreshTokenJpaEntity
import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.infrastructure.mappers.UserMapper
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Repository
class RefreshTokenRepositoryImpl(
    private val mapper: RefreshTokenMapper,
    private val userMapper: UserMapper,
    private val repository: RefreshTokenJpaRepository,
    private val jwtConfig: JwtConfig,
) : RefreshTokenRepository {

    override fun findById(id: UUID): RefreshToken
        = mapper.toModel(
            repository
                .findById(id)
                .orElseThrow { RefreshTokenNotFoundException() }
        )

    override fun findByToken(token: String): RefreshToken
        = mapper.toModel(
            repository
                .findByToken(token)
                .orElseThrow { RefreshTokenNotFoundException() }
        )

    override fun isRefreshTokenActiveByTokenPairId(tokenPairId: String): Boolean
        = repository.findByTokenPairIdAndActiveTrue(tokenPairId).isPresent

    override fun isRefreshTokenActiveInDatabase(token: String): Boolean
        = repository.findByTokenAndActiveTrue(token).isPresent

    override fun save(
        token: String,
        user: User,
        tokenPairId: String,
    ): RefreshToken
        = mapper.toModel(
            repository.save(
                RefreshTokenJpaEntity(
                    token = token,
                    user = userMapper.toEntity(user),
                    tokenPairId = tokenPairId,
                    expiresAt = Instant.now().plus(
                        jwtConfig.refreshTokenExpiration,
                        ChronoUnit.SECONDS
                    )
                )
            )
        )

    override fun deleteById(id: UUID)
        = repository.findById(id).ifPresent { entity -> repository.delete(entity) }

    override fun deactivateToken(token: String)
        = repository.findByToken(token).ifPresent { entity -> repository.delete(entity) }

    override fun deleteExpiredTokens()
        = repository.deleteByExpiresAtBefore(
            Instant.now()
        )

}
