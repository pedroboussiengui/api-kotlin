package org.example.domain.users

import org.example.domain.DomainExceptions
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.*
import org.valiktor.i18n.mapToMessage
import org.valiktor.validate
import java.util.*

// contain entity and value objects

class User(
    val id: Long,
    var username: String,
    var password: String,
    var avatarUrl: String?,
    var email: String,
    var type: UserType,
    var address: Address?
) {
    fun isValid(): Boolean {
        return try {
            validate(this) {
                validate(User::id).isPositive()
                validate(User::username).hasSize(min = 3, max = 80)
                validate(User::password).hasSize(min = 8)
                validate(User::email).isEmail()
                validate(User::type).isIn(UserType.entries)
            }
            address?.isValid() ?: true
        } catch (ex: ConstraintViolationException) {
            val listErrs = ex.constraintViolations
                    .mapToMessage(baseName = "messages", locale = Locale.ENGLISH)
                    .map { "${it.property}: ${it.message}" }
                    .toList()
            throw DomainExceptions.ValidationException("Validation error", listErrs)
        }
    }
}

class Address(
    val cep: String,
    val rua: String,
    val numero: Int,
    val bairro: String,
    val cidade: String,
    val estado: String
) {
    fun isValid(): Boolean {
        return try {
            validate(this) {
                validate(Address::cep).isNotEmpty().matches(Regex("^\\d{5}-?\\d{3}\$"))
                validate(Address::rua).isNotEmpty()
                validate(Address::numero).isPositive()
                validate(Address::bairro).isNotEmpty()
                validate(Address::cidade).isNotEmpty()
                validate(Address::estado).isIn(listOf(
                        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES",
                        "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR",
                        "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC",
                        "SP", "SE", "TO"
                ))
            }
            true
        } catch (ex: ConstraintViolationException) {
            val listErrs = ex.constraintViolations
                    .mapToMessage(baseName = "messages", locale = Locale.ENGLISH)
                    .map { "${it.property}: ${it.message}" }
                    .toList()
            throw DomainExceptions.ValidationException("Validation error", listErrs)
        }
    }
}

enum class UserType {
    USER,           // common application user (post, following, followers, likes)
    MODERATOR,      // use can moderate user content (delete inappropriate posts, ban users)
    ADMIN           // use that have admin roles (register moderator and sysadmin)
}

enum class CredentialType {
    PASSWORD,       // user that authenticate by username and password
    GITHUB          // user that authenticate by GitHub oauth
}

// Credenciais base para o usuário
sealed class Credential(open val type: CredentialType)

// Implementação de credencial com senha
data class PasswordCredential(
        override val type: CredentialType = CredentialType.PASSWORD,
        val password: String
) : Credential(type)

// Implementação de credencial do GitHub (sem senha, só com OAuth)
data class GithubCredential(
        override val type: CredentialType = CredentialType.GITHUB,
        val githubLogin: String,
        val githubId: String
) : Credential(type)