package org.example.application.usecases.user

import org.example.adapter.PasswordHasher
import org.example.adapter.UserCreateReqDto
import org.example.adapter.UserOutput
import org.example.application.Container
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.domain.users.UserType
import kotlin.random.Random

class AddUserUseCase(
        private val userRepository: UserRepository,
        private val passwordHasher: PasswordHasher
) {
    fun execute(input: UserCreateReqDto): Container<Throwable, UserOutput> = Container.catch {
        if (input.password.length < 8)
            throw DomainExceptions.ValidationException("Validation error", listOf("Password should be at least 8 characters"))

        // valida o email direto do input
        if (userRepository.existsByEmail(input.email))
            throw DomainExceptions.ConflictException("E-mail already exists")

        // cria um novo id
        val uuid = Random.nextLong(until = 1_000)
        val user = User(uuid, input.username, input.password, null, input.email, UserType.USER, null)
        // valida, pode lançar uma exceção
        user.isValid()
        // agora que eu tenho certeza que a senha do user tem pelo menos 8 digitos, eu posso gerar um hash
        val hashedPassword = passwordHasher.hash(user.password)
        // atribuio a senha hasheda ao user
        user.password = hashedPassword
        // adiciono do database, pode lançar uma exceção
        userRepository.addUser(user)
        // monto o dto de resposta
        UserOutput(user.id, user.username, user.avatarUrl, user.email)
    }
}