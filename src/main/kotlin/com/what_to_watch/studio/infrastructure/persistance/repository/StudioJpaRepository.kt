package com.what_to_watch.studio.infrastructure.persistance.repository

import com.what_to_watch.studio.infrastructure.persistance.entity.StudioJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface StudioJpaRepository : JpaRepository<StudioJpaEntity, UUID> {

    fun findByName(name: String): Optional<StudioJpaEntity>

}
