package com.what_to_watch.user.domain.repository

import com.what_to_watch.user.domain.model.User
import java.util.UUID

interface UserRepository {

    fun addUser(name: String, passwordHash: String?, discordId: Long, avatarUrl: String): User

    fun saveUser(user: User): User

    fun getUser(id: UUID): User?

    fun getUserByDiscordId(discordId: Long): User?

    fun getAllUsers(): List<User>

    fun deleteUser(id: UUID)

    fun existsByName(name: String): Boolean

}
