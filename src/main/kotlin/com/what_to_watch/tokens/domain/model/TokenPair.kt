package com.what_to_watch.tokens.domain.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val tokenPairId: String
)
