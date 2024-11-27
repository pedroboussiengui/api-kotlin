package org.example.application.usecases.auth

import org.example.adapter.AuthenticationPassowordReqDto
import org.example.adapter.AuthenticationPassowordResDto
import org.example.adapter.InMemoryDAO
import org.example.adapter.PasswordHasher
import org.example.application.Container
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import java.security.SecureRandom
import java.util.*

class PasswordAuthenticationUseCase(
        private val userRepository: UserRepository,
        private val passwordHasher: PasswordHasher,
        private val inMemoryDAO: InMemoryDAO<Long>
) {
    fun execute(input: AuthenticationPassowordReqDto): Container<Throwable, AuthenticationPassowordResDto> = Container.catch {
        val user: User = userRepository.getByEmail(input.email)
                ?: throw DomainExceptions.NotAuthenticatedException("email or password are incorrect")

        val result = passwordHasher.verify(input.password, user.password)
        if (!result) {
            throw DomainExceptions.NotAuthenticatedException("email or password are incorrect")
        }

        // logica gerar token ou gerar a sessão usuário
        val cookieSecret = generateCookieSecret()
        inMemoryDAO.save(cookieSecret, user.id)

        val output = AuthenticationPassowordResDto(cookieSecret, "User successfully authenticated")
        output
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