package org.example.application

sealed class UseCaseResult<out T> {
    data class Success<out T>(val data: T) : UseCaseResult<T>()
    data class NotFoundError(val message: String) : UseCaseResult<String>()
    data class ValidationError(val errors: List<String>) : UseCaseResult<List<String>>()
    data class BusinessRuleError(val message: String) : UseCaseResult<String>()
    data class NotAllowedError(val message: String) : UseCaseResult<String>()
    data class InternalError(val message: String): UseCaseResult<String>()
}