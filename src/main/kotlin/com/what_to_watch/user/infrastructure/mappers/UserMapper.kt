package com.what_to_watch.user.infrastructure.mappers

import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.infrastructure.persistance.entity.UserJpaEntity
import com.what_to_watch.user.infrastructure.web.dto.response.UserDto
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {

    fun toModel(entity: UserJpaEntity): User

    fun toDto(model: User): UserDto

    /**
     * Реализовано вручную: у сущности все поля `val`, а `kotlin-jpa` добавляет no-arg
     * конструктор, который MapStruct выбирает и оставляет объект пустым.
     */
    fun toEntity(model: User): UserJpaEntity =
        UserJpaEntity(
            id = model.id,
            name = model.name,
            passwordHash = model.passwordHash,
            discordId = model.discordId,
            avatarUrl = model.avatarUrl,
        )

}
