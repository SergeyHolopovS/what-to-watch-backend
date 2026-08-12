package com.what_to_watch.exceptions.studio

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus

class StudioRequiredException : BasicException(
    "Нужно указать studioId или studioName",
    HttpStatus.BAD_REQUEST,
)
