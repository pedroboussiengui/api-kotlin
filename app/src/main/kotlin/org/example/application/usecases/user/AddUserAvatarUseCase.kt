package org.example.application.usecases.user

import org.example.adapter.FileHandler
import org.example.adapter.FileReqDto
import org.example.adapter.InMemoryDAO
import org.example.adapter.UserOutput
import org.example.application.Container
import org.example.domain.DomainExceptions
import org.example.domain.users.User
import org.example.domain.users.UserRepository

class AddUserAvatarUseCase(
        private val userRepository: UserRepository,
        private val fileHandler: FileHandler,
        private val inMemoryDAO: InMemoryDAO<Long>
) {
    fun execute(sessionId: String, input: FileReqDto): Container<Throwable, UserOutput> = Container.catch {
        val contextUserId: Long = inMemoryDAO.get(sessionId)
                ?: throw Exception("Invalid session")

        if (input.extension !in listOf(".jpeg", ".jpg", ".png"))
            throw DomainExceptions.ValidationException("Validation error", listOf("Image accept format is jpeg or png"))

        // image service to resize image if too big (400x400)

        if (input.size > 2 * 1024 * 1024)
            throw DomainExceptions.LimitExceededException("Image size has exceeded it max limit of 2MB")

        val user: User = userRepository.getById(contextUserId).getOrThrow()

        // contextUser is authorized to update user?

        val filePath = "user_${user.id}/avatar.${input.extension}"
        val savedPath = fileHandler.upload(filePath, input.content)
                ?: throw Exception("Error during upload image")

        // update use with new url image avatar
        val output: User = userRepository.setAvatar(contextUserId, savedPath).getOrThrow()
        UserOutput(output.id, output.username, output.avatarUrl, output.email)
    }
}