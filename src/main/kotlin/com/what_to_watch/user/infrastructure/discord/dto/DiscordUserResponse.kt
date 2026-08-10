package com.what_to_watch.user.infrastructure.discord.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DiscordUserResponse(

    /** Snowflake, приходит строкой. */
    val id: String,

    val username: String,

    @JsonProperty("global_name")
    val globalName: String? = null,

    /** Хэш аватара, null у пользователей без кастомного аватара. */
    val avatar: String? = null,

)
