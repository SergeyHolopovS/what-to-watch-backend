package com.what_to_watch.series.infrastructure.mappers

import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.infrastructure.persistance.entity.SeriesJpaEntity
import com.what_to_watch.series.infrastructure.web.dto.response.SeriesDto
import org.mapstruct.Mapper

@Mapper
interface SeriesMapper {

    fun toModel(entity: SeriesJpaEntity): Series

    /**
     * Реализовано вручную: у сущности все поля `val`, а `kotlin-jpa` добавляет no-arg
     * конструктор, который MapStruct выбирает и оставляет объект пустым.
     */
    fun toEntity(model: Series): SeriesJpaEntity =
        SeriesJpaEntity(
            id = model.id,
            title = model.title,
            releaseYear = model.releaseYear,
            rating = model.rating,
        )

    fun toDto(model: Series): SeriesDto

    fun toDto(models: List<Series>): List<SeriesDto>

}
