package org.example.application.usecases

import org.example.ApiError
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.infra.sqlite.repositories.SQLiteUserRepository

class UserUpdateReqDto(
    val username: String?,
    val email: String?
)

sealed class UseCaseResult<out T> {
    data class Success<out T>(val data: T) : UseCaseResult<T>()
    data class NotFoundError(val message: String) : UseCaseResult<String>()
    data class ValidationError(val errors: List<String>) : UseCaseResult<List<String>>()
    data class BusinessRuleError(val message: String) : UseCaseResult<String>()
}

class UpdateUserUseCase(
        private val userDb: UserRepository = SQLiteUserRepository()
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
        user.isValid().onFailure {err ->
            if (err is ApiError.ValidationError) return UseCaseResult.ValidationError(err.errors)
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