package org.example.application.usecases.user

import org.example.application.Container
import org.example.domain.users.UserRepository

class RemoveUserByIdUseCase(
        private val userRepository: UserRepository
) {
    fun execute(id: Long): Container<Throwable, Boolean> = Container.catch {
        val status: Boolean = userRepository.remove(id).getOrThrow()
        status
    }
}