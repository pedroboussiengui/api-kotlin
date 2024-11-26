package org.example.domain

sealed class RepositoryExceptions {
    /**
     * when user is not found in persistence implementation
     */
    data class NotFoundException(override val message: String): RuntimeException(message)
}