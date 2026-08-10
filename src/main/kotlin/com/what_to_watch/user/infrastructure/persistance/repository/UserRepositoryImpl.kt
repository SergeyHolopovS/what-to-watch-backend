package com.what_to_watch.user.infrastructure.persistance.repository

import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.domain.repository.UserRepository
import com.what_to_watch.user.infrastructure.mappers.UserMapper
import com.what_to_watch.user.infrastructure.persistance.entity.UserJpaEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
    private val userMapper: UserMapper,
) : UserRepository {

    override fun addUser(name: String, passwordHash: String?, discordId: Long, avatarUrl: String): User {
        val entity = UserJpaEntity(
            name = name,
            passwordHash = passwordHash,
            discordId = discordId,
            avatarUrl = avatarUrl,
        )
        return userMapper.toModel(userJpaRepository.save(entity))
    }

    override fun saveUser(user: User): User {
        val entity = userJpaRepository.save(userMapper.toEntity(user))
        return userMapper.toModel(entity)
    }

    override fun getUser(id: UUID): User? =
        userJpaRepository.findById(id)
            .map(userMapper::toModel)
            .orElse(null)

    override fun getUserByDiscordId(discordId: Long): User? =
        userJpaRepository.findByDiscordId(discordId)?.let(userMapper::toModel)

    override fun getAllUsers(): List<User> =
        userJpaRepository.findAll().map(userMapper::toModel)

    override fun deleteUser(id: UUID) {
        userJpaRepository.deleteById(id)
    }

    override fun existsByName(name: String): Boolean =
        userJpaRepository.existsByName(name)

}
