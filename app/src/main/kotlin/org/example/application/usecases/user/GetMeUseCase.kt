package org.example.application.usecases.user

import org.example.adapter.InMemoryDAO
import org.example.adapter.UserAddressReqDto
import org.example.adapter.UserWithAddressOutput
import org.example.application.Container
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class GetMeUseCase(
        private val userRepository: UserRepository,
        private val inMemoryDAO: InMemoryDAO<Long>
) {
    fun execute(sessionId: String): Container<Throwable, UserWithAddressOutput> = Container.catch {
        val id: Long = inMemoryDAO.get(sessionId)
                ?: throw Exception("Invalid session")

        val user: User = userRepository.getById(id).getOrThrow()
        val address: UserAddressReqDto? = user.address?.let { address ->
            UserAddressReqDto(
                    address.cep,
                    address.rua,
                    address.numero,
                    address.bairro,
                    address.cidade,
                    address.estado
            )
        }
        UserWithAddressOutput(user.id, user.username, user.email, address)
    }
}