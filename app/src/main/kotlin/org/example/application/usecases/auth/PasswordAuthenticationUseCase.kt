package org.example.application.usecases.auth

import org.example.adapter.InMemoryDAO
import org.example.adapter.PasswordHasher
import org.example.application.UseCaseResult
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import java.security.SecureRandom
import java.util.Base64

data class AuthenticationPassowordReqDto(
    val email: String,
    val password: String
)

data class AuthenticationPassowordResDto(
    val cookieSecret: String,
    val message: String
)

class PasswordAuthenticationUseCase(
        private val userRepository: UserRepository,
        private val passwordHasher: PasswordHasher,
        private val inMemoryDAO: InMemoryDAO<Long>
) {
    fun execute(input: AuthenticationPassowordReqDto): UseCaseResult<Any> {
        val user: User = userRepository.getByEmail(input.email)
                ?: return UseCaseResult.NotFoundError("email or password are incorrect")

        val result = passwordHasher.verify(input.password, user.password)
        if (!result) {
            return UseCaseResult.BusinessRuleError("email or password are incorrect")
        }

        // logica gerar token ou gerar a sessão usuário
        val cookieSecret = generateCookieSecret()
        inMemoryDAO.save(cookieSecret, user.id)

//        val output = AuthenticationPassowordResDto(cookieSecret, "User successfully authenticated")
        return UseCaseResult.Success(cookieSecret)
    }

    /**
     * Para o segredo não usaremos algo do tipo UUID porque ele não foi projetado para ser um
     * segredo criptográfico, possui uma certa previsibilidade
     */
    private fun generateCookieSecret(length: Int = 32): String {
        val secureRandom = SecureRandom() // gerador de números aleatórios criptograficamente seguro
        val secretBytes = ByteArray(length) // Gera uma sequência de bytes com o tamanho especificado (por padrão, 32 bytes)
        secureRandom.nextBytes(secretBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(secretBytes) //  Codifica os bytes em uma string Base64 amigável para URLs, sem caracteres de padding (=) ou problemáticos (+, /).
    }
}