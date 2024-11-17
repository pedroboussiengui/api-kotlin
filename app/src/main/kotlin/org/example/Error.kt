package org.example

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")

data class ApiErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: String = LocalDateTime.now().format(formatter).toString(),
    val path: String? = null,
    val subErrors: Any? = null
)

enum class HttpStatus(val code: Int, val message: String) {
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    companion object {
        fun fromCode(code: Int): HttpStatus {
            return entries.firstOrNull { it.code == code } ?: INTERNAL_SERVER_ERROR
        }
    }
}

sealed class ApiError {
    data class ValidationError(val errors: List<String>): Exception()
    data class NotFoundError(override val message: String): Exception(message)
}