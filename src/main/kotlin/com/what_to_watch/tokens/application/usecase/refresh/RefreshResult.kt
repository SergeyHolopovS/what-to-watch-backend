package com.what_to_watch.tokens.application.usecase.refresh

data class RefreshResult(
    val accessToken: String,
    val refreshToken: String
)
