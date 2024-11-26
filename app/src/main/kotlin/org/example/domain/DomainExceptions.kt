package org.example.domain

/**
 * validation domain sealed class
 */
sealed class DomainExceptions {
    /**
     * regras de negócio violadas
     */
    open class BusinessRuleException(override val message: String): RuntimeException(message)

    /**
     * entidade inválidas
     */
    data class ValidationException(override val message: String, val errors: List<String>): BusinessRuleException(message)

    /**
     * Violação da restrição de unicidade
     */
    data class ConflictException(override val message: String): BusinessRuleException(message)

    /**
     * Violação de tamanho máximo de arquivo
     */
    data class LimitExceededException(override val message: String): BusinessRuleException(message)

    /**
     * when authorization police fails
     */
    data class NotAllowedException(override val message: String): RuntimeException(message)
}