package org.example.application.usecases.user

import org.example.application.UseCaseResult
import org.example.adapter.FileHandler
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import java.io.InputStream

data class FileReqDto(
    val content: InputStream,
    val size: Long,
    val extension: String
)

class AddUserAvatarUseCase(
        private val userRepository: UserRepository,
        private val fileHandler: FileHandler
) {
    fun execute(contextUser: Long, input: FileReqDto): UseCaseResult<Any> {
        if (input.extension !in listOf(".jpeg", ".jpg", ".png")) {
            return UseCaseResult.BusinessRuleError("Image accept format is jpeg or png")
        }

        // image service to resize image if too big (400x400)

        if (input.size > 2 * 1024 * 1024) {
            return UseCaseResult.BusinessRuleError("Image size has exceeded it max limit of 2MB")
        }
        val user: User = userRepository.getById(contextUser).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )

        // contextUser is authorized to update user?

        val filePath = "user_${user.id}/avatar.${input.extension}"
        val savedPath = fileHandler.upload(filePath, input.content)
                ?: return UseCaseResult.InternalError("Erro duting image saving")

        // update use with new url image avatar
        val output: User = userRepository.setAvatar(contextUser, savedPath).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )
        return UseCaseResult.Success(output)
    }
}