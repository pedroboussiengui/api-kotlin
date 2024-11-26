package org.example.application

sealed class ApplicationException<out T> {
    data class NotFoundError(override val message: String) : Exception(message)
    data class ValidationError(val errors: List<String>) : Exception()
    data class BusinessRuleError(override val message: String) : Exception(message)
    data class NotAllowedError(override val message: String) : Exception(message)
    data class InternalError(override val message: String): Exception(message)
}

sealed class Container<out L, out R> {

    data class Failure<L> (val value: L) : Container<L, Nothing>()

    data class Success<R> (val value: R) : Container<Nothing, R>()

    companion object {
        inline fun <R> catch(block: () -> R): Container<Throwable, R> {
            return try {
                Success(block())
            } catch (e: Throwable) {
                Failure(e)
            }
        }
    }
}