package org.example.application.usecases.user

import org.example.adapter.UserAddressReqDto
import org.example.adapter.UserWithAddressOutput
import org.example.application.Container
import org.example.domain.users.Address
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class SetAddressUserUseCase(
        private val userRepository: UserRepository
) {
    fun execute(id: Long, req: UserAddressReqDto): Container<Throwable, UserWithAddressOutput> = Container.catch {
        // instancio um novo value objet address com os valores do dto
        val address = Address(
                req.cep,
                req.rua,
                req.numero,
                req.bairro,
                req.cidade,
                req.estado
        )
        // pode lança uma exceção ao validar
        address.isValid()
        val user: User = userRepository.setAddress(id, address).getOrThrow()
        UserWithAddressOutput(user.id, user.username, user.email, req)
    }
}