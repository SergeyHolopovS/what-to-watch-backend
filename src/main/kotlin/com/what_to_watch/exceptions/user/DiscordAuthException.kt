package com.what_to_watch.exceptions.user

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class DiscordAuthException(message: String) : BasicException(
    message,
    HttpStatus.UNAUTHORIZED,
)
