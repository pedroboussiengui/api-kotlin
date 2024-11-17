package org.example

data class ValidationError(val errors: List<String>): Exception()

data class NotFoundError(override val message: String): Exception(message)

data class ApiError(
    val status: Int,
    val message: String,
    val path: String? = null,
    val subErrors: List<String> = emptyList()
)