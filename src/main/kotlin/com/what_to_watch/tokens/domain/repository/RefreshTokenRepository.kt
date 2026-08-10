package com.what_to_watch.tokens.domain.repository

import com.what_to_watch.tokens.domain.model.RefreshToken
import com.what_to_watch.user.domain.model.User
import java.util.UUID

interface RefreshTokenRepository {

    fun findById(id: UUID): RefreshToken

    fun findByToken(token: String): RefreshToken

    fun isRefreshTokenActiveByTokenPairId(tokenPairId: String): Boolean

    fun isRefreshTokenActiveInDatabase(token: String): Boolean

    fun deleteById(id: UUID)

    fun save(
        token: String,
        user: User,
        tokenPairId: String,
    ): RefreshToken

    fun deactivateToken(token: String)

    fun deleteExpiredTokens()

}
