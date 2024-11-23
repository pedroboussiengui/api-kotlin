package org.example.application.usecases.user

import org.example.domain.users.User
import org.example.domain.users.UserRepository

class GetAllUsersUseCase(
        private val userRepository: UserRepository
) {
    fun execute(): List<User> {
        return userRepository.getAll()
    }
}