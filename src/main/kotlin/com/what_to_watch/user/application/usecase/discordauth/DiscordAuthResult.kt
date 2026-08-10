package com.what_to_watch.user.application.usecase.discordauth

import com.what_to_watch.user.domain.model.User

data class DiscordAuthResult(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
