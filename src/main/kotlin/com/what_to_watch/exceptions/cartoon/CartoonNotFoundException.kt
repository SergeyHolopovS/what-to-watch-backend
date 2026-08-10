package com.what_to_watch.exceptions.cartoon

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus
import java.util.UUID

class CartoonNotFoundException(id: UUID) : BasicException(
    "Мультфильм с id $id не найден",
    HttpStatus.NOT_FOUND,
)
