package com.what_to_watch.exceptions.user

import com.what_to_watch.exceptions.basic.BasicException
import org.springframework.http.HttpStatus
import java.util.UUID

class UserNotFoundException(id: UUID) : BasicException(
    "Пользователь с id $id не найден",
    HttpStatus.NOT_FOUND,
)
