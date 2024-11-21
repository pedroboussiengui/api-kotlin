package org.example.domain

/**
 * validation,
 */
sealed class DomainExceptions {

    data class ValidationError(val errors: List<String>): Exception()
}