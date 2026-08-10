package com.what_to_watch.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class CookieConfig(
    @Value($$"${cookies.access-token-name}")
    val accessTokenName: String,
    @Value($$"${cookies.refresh-token-name}")
    val refreshTokenName: String,
    @Value($$"${cookies.secure}")
    val secure: Boolean,
    @Value($$"${cookies.same-site}")
    val sameSite: String,
    @Value($$"${cookies.refresh-path}")
    val refreshPath: String,
)
