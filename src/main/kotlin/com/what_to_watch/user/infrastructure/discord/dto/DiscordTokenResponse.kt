package com.what_to_watch.user.infrastructure.discord.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DiscordTokenResponse(

    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

)
