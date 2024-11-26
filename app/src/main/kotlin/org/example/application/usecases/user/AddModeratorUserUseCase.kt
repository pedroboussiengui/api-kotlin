package org.example.application.usecases.user

import org.example.adapter.ModUserCreateReqDto
import org.example.adapter.ModUserCreateResDto
import org.example.application.Container
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.domain.users.UserType
import kotlin.random.Random

class AddModeratorUserUseCase(
        private val userRepository: UserRepository
) {
    fun execute(input: ModUserCreateReqDto): Container<Throwable, ModUserCreateResDto> = Container.catch {
        if (userRepository.existsByEmail(input.email))
            throw DomainExceptions.ConflictException("E-mail already exists")

        val uuid = Random.nextLong(from = 1_001, until = 10_000)
        val user = User(uuid, input.username, input.password, null, input.email, UserType.MODERATOR, null)
        user.isValid()
        userRepository.addUser(user)
        ModUserCreateResDto(user.id, user.username, user.email)
    }
}