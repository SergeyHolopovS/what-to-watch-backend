package com.what_to_watch.exceptions.tokens

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class RefreshTokenNotFoundException : BasicException(
    "Refresh токен не найден",
    HttpStatus.NOT_FOUND,
)
