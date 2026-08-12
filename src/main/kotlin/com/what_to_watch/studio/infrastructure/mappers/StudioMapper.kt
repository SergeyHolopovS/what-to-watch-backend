package com.what_to_watch.studio.infrastructure.mappers

import com.what_to_watch.studio.domain.model.Studio
import com.what_to_watch.studio.infrastructure.persistance.entity.StudioJpaEntity
import com.what_to_watch.studio.infrastructure.web.dto.response.StudioDto
import org.mapstruct.Mapper

@Mapper
interface StudioMapper {

    fun toModel(entity: StudioJpaEntity): Studio

    /**
     * Реализовано вручную: у сущности все поля `val`, а `kotlin-jpa` добавляет no-arg
     * конструктор, который MapStruct выбирает и оставляет объект пустым.
     */
    fun toEntity(model: Studio): StudioJpaEntity =
        StudioJpaEntity(
            id = model.id,
            name = model.name,
        )

    fun toDto(model: Studio): StudioDto

    fun toDto(models: List<Studio>): List<StudioDto>

}
