package org.example.application.usecases.user

import org.example.adapter.InMemoryDAO
import org.example.application.UseCaseResult
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class GetMeUseCase(
        private val userRepository: UserRepository,
        private val inMemoryDAO: InMemoryDAO<Long>
) {
    fun execute(sessionId: String): UseCaseResult<Any> {
        // recover userId from session
        val id: Long = inMemoryDAO.get(sessionId)
        val user: User = userRepository.getById(id).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        return UseCaseResult.Success(user)
    }
}