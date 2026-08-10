package com.what_to_watch.exceptions.dto

import org.springframework.http.HttpStatus

data class ErrorDto(
    val message: String,
    val status: HttpStatus,
)
