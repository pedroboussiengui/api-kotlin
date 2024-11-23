package org.example.adapter

/**
 * Essa interface representa o contrato de como deve ser a hasheada uma senha
 */
interface PasswordHasher {
    fun hash(password: String): String
    fun verify(password: String, hashedPassword: String): Boolean
}