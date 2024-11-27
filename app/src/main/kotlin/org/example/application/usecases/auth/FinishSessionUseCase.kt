package org.example.application.usecases.auth

import org.example.adapter.FishshSessionOutput
import org.example.adapter.InMemoryDAO
import org.example.application.Container

class FinishSessionUseCase(
        private val inMemoryDAO: InMemoryDAO<Long>
) {
    fun execute(sessionId: String): Container<Throwable, FishshSessionOutput> = Container.catch {
        val id: Long = inMemoryDAO.get(sessionId)
                ?: throw Exception("Invalid session")

        inMemoryDAO.delete(sessionId)

        FishshSessionOutput("Session finished successfully")
    }
}
