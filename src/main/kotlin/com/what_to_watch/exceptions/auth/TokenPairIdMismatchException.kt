package com.what_to_watch.exceptions.auth

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class TokenPairIdMismatchException : BasicException(
    "Токены принадлежат разным парам",
    HttpStatus.UNAUTHORIZED,
)
