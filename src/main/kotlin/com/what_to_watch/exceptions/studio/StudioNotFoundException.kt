package com.what_to_watch.exceptions.studio

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus
import java.util.UUID

class StudioNotFoundException(id: UUID) : BasicException(
    "Студия с id $id не найдена",
    HttpStatus.NOT_FOUND,
)
