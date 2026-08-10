package com.what_to_watch.exceptions.basic

import org.springframework.http.HttpStatus

open class BasicException(
    override val message: String,
    open val status: HttpStatus
): RuntimeException(message)