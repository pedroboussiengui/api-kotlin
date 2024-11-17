package org.example

import org.valiktor.ConstraintViolationException
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isPositive
import org.valiktor.functions.matches
import org.valiktor.i18n.mapToMessage
import org.valiktor.validate
import java.util.*

class User(
    val id: Long,
    var username: String,
    var password: String,
    var email: String
) {
    fun isValid(): Result<Boolean> {
        try {
            validate(this) {
                validate(User::id).isPositive()
                validate(User::username).hasSize(min = 3, max = 80)
                validate(User::password).hasSize(min = 8).matches(Regex("^[a-zA-Z0-9]*$"))
                validate(User::email).isEmail()
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