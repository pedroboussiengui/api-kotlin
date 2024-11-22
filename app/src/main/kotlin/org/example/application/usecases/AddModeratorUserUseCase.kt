package org.example.application.usecases

import org.example.application.UseCaseResult
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.domain.users.UserType
import kotlin.random.Random

data class ModUserCreateReqDto(
        val username: String,
        val password: String,
        val email: String
)

data class ModUserCreateResDto(
        var id: Long,
        var username: String,
        var email: String
)

class AddModeratorUserUseCase(
        private val userRepository: UserRepository
) {
    fun execute(input: ModUserCreateReqDto): UseCaseResult<Any> {
        val uuid = Random.nextLong(from = 1_001, until = 10_000)
        val user = User(uuid, input.username, input.password, input.email, UserType.MODERATOR, null)

        if (userRepository.existsByEmail(user.email)) {
            return UseCaseResult.BusinessRuleError("E-mail already exists")
        }

        user.isValid().onFailure { err ->
            if (err is DomainExceptions.ValidationError) return UseCaseResult.ValidationError(err.errors)
        }

        userRepository.addUser(user)
        val output = ModUserCreateResDto(user.id, user.username, user.email)
        return UseCaseResult.Success(output)
    }
}