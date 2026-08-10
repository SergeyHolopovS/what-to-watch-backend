package com.what_to_watch.user.domain.service

interface AccessWhitelist {

    fun isAllowed(discordId: Long): Boolean

}
