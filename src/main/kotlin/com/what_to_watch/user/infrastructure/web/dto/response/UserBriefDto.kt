package com.what_to_watch.user.infrastructure.web.dto.response

import java.util.UUID

data class UserBriefDto(
    val id: UUID,
    val avatarUrl: String,
)
