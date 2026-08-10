package com.what_to_watch.exceptions.series

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class NoSeriesToWatchException : BasicException(
    "Не осталось неоценённых сериалов",
    HttpStatus.NOT_FOUND,
)
