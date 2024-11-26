package org.example.application.usecases.user

import org.example.adapter.UserUpdateReqDto
import org.example.application.Container
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class UpdateUserUseCase(
        private val userDb: UserRepository
) {
    fun execute(id: Long, input: UserUpdateReqDto): Container<Throwable, Long> = Container.catch {
        // tenta buscar o user pelo id
        val user: User = userDb.getById(id).getOrThrow()
        // verifica se o email já existe
        val existingUser: User? = userDb.getByEmail(user.email)
        if (existingUser != null && existingUser.id != id) {
            throw DomainExceptions.ConflictException("E-mail already exists")
        }

        // atualiza os campos passados
        input.username?.let { user.username = it }
        input.email?.let { user.email = it }

        // valida
        user.isValid()
        // atualiza e devolve o id
        val updatedUserId: Long = userDb.update(id, user).getOrThrow()
        updatedUserId
    }
}