package com.what_to_watch.exceptions.cartoon

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class CartoonAlreadyExistsException(title: String) : BasicException(
    "Мультфильм \"$title\" уже добавлен",
    HttpStatus.CONFLICT,
)
