package org.example.application.usecases

import org.example.domain.users.User
import org.example.domain.users.UserRepository

class GetUserByIdUseCase (
    private val userDb: UserRepository
) {
    fun execute(id: Long): UseCaseResult<Any> {
        val user: User = userDb.getById(id).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        return UseCaseResult.Success(user)
    }
}