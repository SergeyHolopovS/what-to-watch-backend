package com.what_to_watch.tokens.domain.service

import com.what_to_watch.tokens.domain.model.TokenPair
import com.what_to_watch.user.domain.model.User
import java.util.UUID

interface TokenService {

    fun generateTokenPair(user: User): TokenPair

    fun isTokenValid(token: String, user: User): Boolean

    fun extractTokenPairId(token: String, ignoreExpiration: Boolean = false): String?

    fun extractId(token: String, ignoreExpiration: Boolean = false): UUID

    fun isRefreshTokenValid(token: String, user: User): Boolean

    fun isAccessTokenExpired(token: String): Boolean

}
