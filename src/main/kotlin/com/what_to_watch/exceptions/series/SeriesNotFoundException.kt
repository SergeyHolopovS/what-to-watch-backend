package com.what_to_watch.exceptions.series

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus
import java.util.UUID

class SeriesNotFoundException(id: UUID) : BasicException(
    "Сериал с id $id не найден",
    HttpStatus.NOT_FOUND,
)
