package org.example.application.usecases.user

import org.example.application.UseCaseResult
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository

data class UserUpdateReqDto(
    val username: String?,
    val email: String?
)

class UpdateUserUseCase(
        private val userDb: UserRepository
) {
    fun execute(id: Long, input: UserUpdateReqDto): UseCaseResult<Any> {
        val user: User = userDb.getById(id).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        input.username?.let { user.username = it }
        input.email?.let { user.email = it }
        val existingUser: User? = userDb.getByEmail(user.email)
        if (existingUser != null && existingUser.id != id) {
            return UseCaseResult.BusinessRuleError("E-mail already exists")
        }
        user.isValid().onFailure { err ->
            if (err is DomainExceptions.ValidationError) return UseCaseResult.ValidationError(err.errors)
        }
        val updatedUserId: Long = userDb.update(id, user).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        return UseCaseResult.Success(updatedUserId)
    }
}