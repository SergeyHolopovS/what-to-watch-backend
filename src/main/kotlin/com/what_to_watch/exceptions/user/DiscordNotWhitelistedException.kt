package com.what_to_watch.exceptions.user

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class DiscordNotWhitelistedException : BasicException(
    "Ваш аккаунт Discord не допущен к сервису",
    HttpStatus.FORBIDDEN,
)
