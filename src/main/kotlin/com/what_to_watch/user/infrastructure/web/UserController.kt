package com.what_to_watch.user.infrastructure.web

import com.what_to_watch.configs.BEARER_AUTH
import com.what_to_watch.configs.COOKIE_AUTH
import com.what_to_watch.configs.USERS_TAG
import com.what_to_watch.user.domain.model.User
import com.what_to_watch.user.infrastructure.web.dto.response.UserBriefDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = USERS_TAG)
@RestController
@RequestMapping("/users")
class UserController {

    @Operation(
        summary = "Краткая информация о текущем пользователе",
        description = "Id и url на аватар в Discord текущего пользователя, определённого по токену.",
    )
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal user: User): UserBriefDto =
        UserBriefDto(
            id = user.id,
            avatarUrl = user.avatarUrl,
        )

}
