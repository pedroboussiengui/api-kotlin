package org.example.application.usecases.auth

import org.example.adapter.PasswordHasher
import org.example.application.UseCaseResult
import org.example.domain.users.User
import org.example.domain.users.UserRepository

data class AuthenticationPassowordReqDto(
    val email: String,
    val password: String
)

data class AuthenticationPassowordResDto(
    val message: String
)

class PasswordAuthenticationUseCase(
        private val userRepository: UserRepository,
        private val passwordHasher: PasswordHasher
) {
    fun execute(input: AuthenticationPassowordReqDto): UseCaseResult<Any> {
        val user: User = userRepository.getByEmail(input.email)
                ?: return UseCaseResult.NotFoundError("email or password are incorrect")

        val result = passwordHasher.verify(input.password, user.password)
        if (!result) {
            return UseCaseResult.BusinessRuleError("email or password are incorrect")
        }

        // logica gerar token ou gerar a sessão usuário

        val output = AuthenticationPassowordResDto("User successfully authenticated")
        return UseCaseResult.Success(output)
    }
}