package org.example.application.usecases

import org.example.ApiError
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import kotlin.random.Random

data class UserCreateReqDto(
    val username: String,
    val password: String,
    val email: String
)

data class UserCreateResDto(
    var id: Long,
    var username: String,
    var email: String
)

class AddUserUseCase(
        private val userRepository: UserRepository
) {
    fun execute(input: UserCreateReqDto): UseCaseResult<Any> {
        val uuid = Random.nextLong(until = 1_000)
        val user = User(uuid, input.username, input.password, input.email, null)

        if (userRepository.existsByEmail(user.email)) {
            return UseCaseResult.BusinessRuleError("E-mail already exists")
        }

        user.isValid().onFailure {err ->
            if (err is ApiError.ValidationError) return UseCaseResult.ValidationError(err.errors)
        }

        userRepository.addUser(user)
        val output = UserCreateResDto(user.id, user.username, user.email)
        return UseCaseResult.Success(output)
    }
}