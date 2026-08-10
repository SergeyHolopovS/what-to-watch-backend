package com.what_to_watch.exceptions.auth

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class AccessStillActiveException : BasicException(
    "Access токен ещё активен",
    HttpStatus.BAD_REQUEST,
)
