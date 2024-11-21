package org.example.application.usecases

import org.example.application.UseCaseResult
import org.example.domain.DomainExceptions
import org.example.domain.posts.Post
import org.example.domain.posts.PostRepository
import kotlin.random.Random

data class PostCreateReqDto(
    val title: String,
    val content: String
)

class CreatePostUseCase(
        private val postRepository: PostRepository
) {
    fun execute(input: PostCreateReqDto, owner: Long): UseCaseResult<Any> {
        val uuid = Random.nextLong(until = 1_000)
        val post = Post(uuid, input.title, input.content, owner)

        post.isValid().onFailure { err ->
            if (err is DomainExceptions.ValidationError) return UseCaseResult.ValidationError(err.errors)
        }

        val createdIdPost: Long = postRepository.create(post).fold(
                onSuccess = { it },
                onFailure = { err ->
                    return UseCaseResult.NotFoundError(err.message!!)
                }
        )

        return UseCaseResult.Success(createdIdPost)
    }
}