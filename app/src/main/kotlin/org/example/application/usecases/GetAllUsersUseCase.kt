package org.example.application.usecases

import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.infra.sqlite.repositories.SQLiteUserRepository

class GetAllUsersUseCase(
        private val userDb: UserRepository = SQLiteUserRepository()
) {
    fun execute(): List<User> {
        return userDb.getAll()
    }
}