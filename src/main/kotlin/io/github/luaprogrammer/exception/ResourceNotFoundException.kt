package io.github.luaprogrammer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(exception: String?) : RuntimeException(exception) {
    companion object {
        private const val serialVersionUID = 1L
    }
}