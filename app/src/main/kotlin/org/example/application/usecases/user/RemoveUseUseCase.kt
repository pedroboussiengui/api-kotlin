package org.example.application.usecases.user

import org.example.application.UseCaseResult
import org.example.domain.users.UserRepository

class RemoveUseUseCase(
        private val userDb: UserRepository
) {
    fun execute(id: Long): UseCaseResult<Any> {
        val status: Boolean = userDb.remove(id).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        return UseCaseResult.Success(status)
    }
}