package com.what_to_watch.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class FrontendConfig(
    @Value($$"${frontend.redirect-uri}")
    val redirectUri: String,
)
