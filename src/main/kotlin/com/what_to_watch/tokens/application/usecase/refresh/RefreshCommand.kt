package com.what_to_watch.tokens.application.usecase.refresh

data class RefreshCommand(
    val accessToken: String,
    val refreshToken: String
)
