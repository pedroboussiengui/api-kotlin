package org.example.application.usecases.user

import org.example.application.UseCaseResult
import org.example.domain.DomainExceptions
import org.example.domain.users.Address
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class UserAddressReqDto(
    val cep: String,
    val rua: String,
    val numero: Int,
    val bairro: String,
    val cidade: String,
    val estado: String
)

class SetAddressUserUseCase(
        private val userRepository: UserRepository
) {
    fun execute(id: Long, req: UserAddressReqDto): UseCaseResult<Any> {
        val address = Address(
                req.cep,
                req.rua,
                req.numero,
                req.bairro,
                req.cidade,
                req.estado
        )
        // a lógica aqui está invertida, a validação deve ocorrer antes da persistencia no banco de dados
        val user: User = userRepository.setAddress(id, address).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        user.isValid().onFailure {err ->
            if (err is DomainExceptions.ValidationError) return UseCaseResult.ValidationError(err.errors)
        }
        return UseCaseResult.Success(user)
    }
}