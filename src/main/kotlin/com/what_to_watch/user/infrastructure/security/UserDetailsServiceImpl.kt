package com.what_to_watch.user.infrastructure.security

import com.what_to_watch.exceptions.auth.TokenUnauthorizedException
import com.what_to_watch.user.domain.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserDetailsServiceImpl(
    private val repository: UserRepository,
) : UserDetailsService {

    // Username в данном проекте эквивалентен id: в токене лежит именно id пользователя
    override fun loadUserByUsername(username: String): UserDetails {
        try {
            return repository.getUser(UUID.fromString(username))
                ?: throw TokenUnauthorizedException("Токен невалиден")
        } catch (_: IllegalArgumentException) {
            throw TokenUnauthorizedException("Токен невалиден")
        }
    }

}
