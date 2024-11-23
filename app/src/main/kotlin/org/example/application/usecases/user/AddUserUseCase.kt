package org.example.application.usecases.user

import org.example.adapter.PasswordHasher
import org.example.application.UseCaseResult
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.domain.users.UserType
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
        private val userRepository: UserRepository,
        private val passwordHasher: PasswordHasher
) {
    fun execute(input: UserCreateReqDto): UseCaseResult<Any> {
        val uuid = Random.nextLong(until = 1_000)
        val user = User(uuid, input.username, input.password, null, input.email, UserType.USER, null)

        if (userRepository.existsByEmail(user.email)) {
            return UseCaseResult.BusinessRuleError("E-mail already exists")
        }

        user.isValid().onFailure {err ->
            if (err is DomainExceptions.ValidationError) return UseCaseResult.ValidationError(err.errors)
        }

        // agora que eu tenho certeza que a senha do user tem pelo menos 8 digitos, eu posso gerar um hash
        val hashedPassword = passwordHasher.hash(user.password)

        user.password = hashedPassword

        userRepository.addUser(user)
        val output = UserCreateResDto(user.id, user.username, user.email)
        return UseCaseResult.Success(output)
    }
}