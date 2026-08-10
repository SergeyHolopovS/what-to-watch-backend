package com.what_to_watch.user.infrastructure.persistance.repository

import com.what_to_watch.user.infrastructure.persistance.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {

    fun findByDiscordId(discordId: Long): UserJpaEntity?

    fun existsByName(name: String): Boolean

}
