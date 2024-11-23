package org.example.application.usecases.user

import org.example.application.UseCaseResult
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class GetUserByIdUseCase (
    private val userRepository: UserRepository
) {
    fun execute(id: Long): UseCaseResult<Any> {
        val user: User = userRepository.getById(id).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        return UseCaseResult.Success(user)
    }
}