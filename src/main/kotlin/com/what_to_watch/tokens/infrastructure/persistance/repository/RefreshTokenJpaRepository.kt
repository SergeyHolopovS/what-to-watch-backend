package com.what_to_watch.tokens.infrastructure.persistance.repository

import com.what_to_watch.tokens.infrastructure.persistance.entity.RefreshTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Optional
import java.util.UUID

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, UUID> {

    fun findByToken(token: String): Optional<RefreshTokenJpaEntity>

    fun findByTokenPairIdAndActiveTrue(tokenPairId: String): Optional<RefreshTokenJpaEntity>

    fun findByTokenAndActiveTrue(token: String): Optional<RefreshTokenJpaEntity>

    @Transactional
    fun deleteByExpiresAtBefore(dateTime: Instant?)

}
