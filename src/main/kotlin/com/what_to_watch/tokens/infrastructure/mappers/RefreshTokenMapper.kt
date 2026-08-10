package com.what_to_watch.tokens.infrastructure.mappers

import com.what_to_watch.tokens.domain.model.RefreshToken
import com.what_to_watch.tokens.infrastructure.persistance.entity.RefreshTokenJpaEntity
import com.what_to_watch.user.infrastructure.mappers.UserMapper
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(uses = [UserMapper::class], unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface RefreshTokenMapper {

    fun toModel(entity: RefreshTokenJpaEntity): RefreshToken

}
