package com.what_to_watch.exceptions.auth

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class RefreshTokenInvalidException : BasicException(
    "Refresh токен невалиден",
    HttpStatus.UNAUTHORIZED,
)
