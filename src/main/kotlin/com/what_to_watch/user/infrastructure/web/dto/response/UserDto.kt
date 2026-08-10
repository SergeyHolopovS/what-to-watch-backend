package com.what_to_watch.user.infrastructure.web.dto.response

import java.util.UUID

data class UserDto(
    val id: UUID,
    val name: String,
    val discordId: Long,
)
