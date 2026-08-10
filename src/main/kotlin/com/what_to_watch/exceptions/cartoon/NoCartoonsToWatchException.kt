package com.what_to_watch.exceptions.cartoon

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class NoCartoonsToWatchException : BasicException(
    "Не осталось неоценённых мультфильмов",
    HttpStatus.NOT_FOUND,
)
