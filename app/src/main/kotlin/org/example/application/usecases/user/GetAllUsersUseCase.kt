package org.example.application.usecases.user

import org.example.adapter.UserOutput
import org.example.application.Container
import org.example.domain.users.UserRepository

class GetAllUsersUseCase(
        private val userRepository: UserRepository
) {
    fun execute(): Container<Throwable, List<UserOutput>> = Container.catch {
        val output: MutableList<UserOutput> = mutableListOf()
        // posteriormente essa busca ira ser paginada
        userRepository.getAll().map {
            output.add(UserOutput(it.id, it.username, it.avatarUrl, it.email))
        }
        output
    }
}