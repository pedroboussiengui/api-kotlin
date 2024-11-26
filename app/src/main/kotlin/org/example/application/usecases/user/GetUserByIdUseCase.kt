package org.example.application.usecases.user

import org.example.adapter.UserOutput
import org.example.application.Container
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class GetUserByIdUseCase (
    private val userRepository: UserRepository
) {
    fun execute(id: Long): Container<Throwable, UserOutput> = Container.catch {
        val user: User = userRepository.getById(id).getOrThrow()
        UserOutput(user.id, user.username, user.avatarUrl, user.email)
    }
}