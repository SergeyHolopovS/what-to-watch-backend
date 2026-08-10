package com.what_to_watch.exceptions.series

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class SeriesAlreadyExistsException(title: String) : BasicException(
    "Сериал \"$title\" уже добавлен",
    HttpStatus.CONFLICT,
)
