package com.what_to_watch.user.infrastructure.discord

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "discord.oauth")
data class DiscordOAuthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val scope: String = "identify",
    val authorizationUri: String = "https://discord.com/oauth2/authorize",
    val tokenUri: String = "https://discord.com/api/v10/oauth2/token",
    val userInfoUri: String = "https://discord.com/api/v10/users/@me",
)
