package com.what_to_watch.tokens.domain.model

import com.what_to_watch.user.domain.model.User
import java.time.Instant
import java.util.UUID

data class RefreshToken(
    val id: UUID,
    val token: String,
    val user: User,
    val tokenPairId: String,
    val active: Boolean,
    val createdAt: Instant,
    val expiresAt: Instant
)
