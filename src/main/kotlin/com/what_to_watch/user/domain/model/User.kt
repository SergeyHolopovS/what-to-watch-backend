package com.what_to_watch.user.domain.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class User(

    val id: UUID,

    val name: String,

    val passwordHash: String?,

    val discordId: Long,

    val avatarUrl: String,

) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf()
    override fun getPassword(): String? = passwordHash
    override fun getUsername(): String = name
    override fun isEnabled(): Boolean = true
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true

}
