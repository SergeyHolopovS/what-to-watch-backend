package com.what_to_watch.studio.infrastructure.persistance.repository

import com.what_to_watch.studio.domain.model.Studio
import com.what_to_watch.studio.domain.repository.StudioRepository
import com.what_to_watch.studio.infrastructure.mappers.StudioMapper
import com.what_to_watch.studio.infrastructure.persistance.entity.StudioJpaEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class StudioRepositoryImpl(
    private val studioJpaRepository: StudioJpaRepository,
    private val studioMapper: StudioMapper,
) : StudioRepository {

    override fun addStudio(name: String): Studio {
        val entity = StudioJpaEntity(name = name)
        return studioMapper.toModel(studioJpaRepository.save(entity))
    }

    override fun getStudio(id: UUID): Studio? =
        studioJpaRepository.findById(id)
            .map(studioMapper::toModel)
            .orElse(null)

    override fun getAllStudios(): List<Studio> =
        studioJpaRepository.findAll().map(studioMapper::toModel)

}
