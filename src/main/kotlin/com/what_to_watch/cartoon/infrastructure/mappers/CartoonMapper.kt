package com.what_to_watch.cartoon.infrastructure.mappers

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.infrastructure.persistance.entity.CartoonJpaEntity
import com.what_to_watch.cartoon.infrastructure.web.dto.response.CartoonDto
import org.mapstruct.Mapper

@Mapper
interface CartoonMapper {

    fun toModel(entity: CartoonJpaEntity): Cartoon

    /**
     * Реализовано вручную: у сущности все поля `val`, а `kotlin-jpa` добавляет no-arg
     * конструктор, который MapStruct выбирает и оставляет объект пустым.
     */
    fun toEntity(model: Cartoon): CartoonJpaEntity =
        CartoonJpaEntity(
            id = model.id,
            title = model.title,
            releaseYear = model.releaseYear,
            rating = model.rating,
            genre = model.genre,
            duration = model.duration,
            type = model.type,
        )

    fun toDto(model: Cartoon): CartoonDto

    fun toDto(models: List<Cartoon>): List<CartoonDto>

}
