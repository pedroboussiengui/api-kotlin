package org.example;

import org.valiktor.validate
import org.valiktor.functions.*
import org.valiktor.ConstraintViolationException

class User(
    var id: Long,
    val username: String,
    val password: String,
    val email: String
) {
    fun validate() {
        try {
            validate(this): Result<Boolean> {
                validate(User::id).isPositive()
                validate(User::username).hasSize(min = 3, max = 80).matches(Regex("^[a-zA-Z0-9]*$"))
                validate(User::password).hasSize(min = 8).matches(Regex("^[a-zA-Z0-9]*$"))
                validate(User::email).isEmail()
            }
        } catch (ex: ConstraintViolationException) {
            ex.constraintViolations
                .map { "${it.property}: ${it.constraint.name}" }
                .forEach(::println)
        }
    }
}