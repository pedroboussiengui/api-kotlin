package org.example.domain.users

import org.example.ApiError
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
    var email: String,
    var address: Address?
) {
    fun isValid(): Result<Boolean> {
        try {
            validate(this) {
                validate(User::id).isPositive()
                validate(User::username).hasSize(min = 3, max = 80)
                validate(User::password).hasSize(min = 8).matches(Regex("^[a-zA-Z0-9]*$"))
                validate(User::email).isEmail()
                validate(User::address).validate {
                    validate(Address::cep).isNotEmpty().matches(Regex("^\\d{5}-?\\d{3}\$"))
                    validate(Address::rua).isNotEmpty()
                    validate(Address::numero).isPositive()
                    validate(Address::bairro).isNotEmpty()
                    validate(Address::cidade).isNotEmpty()
                    validate(Address::estado).isIn(listOf("RN","CE","PB","PE"))
                }
            }
            return Result.success(true)
        } catch (ex: ConstraintViolationException) {
            val listErrs = ex.constraintViolations
                .mapToMessage(baseName = "messages", locale = Locale.ENGLISH)
                .map { "${it.property}: ${it.message}" }
                .toList()
            return Result.failure(ApiError.ValidationError(listErrs))
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
)
