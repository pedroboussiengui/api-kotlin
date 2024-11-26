package org.example.application.usecases.post

import org.example.adapter.PostCreateReqDto
import org.example.application.ApplicationException
import org.example.domain.Action
import org.example.domain.DomainExceptions
import org.example.domain.PostAuthorizationPolicy
import org.example.domain.RepositoryExceptions
import org.example.domain.posts.Post
import org.example.domain.posts.PostRepository
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import kotlin.random.Random

class CreatePostUseCase(
        private val postRepository: PostRepository,
        private val userRepository: UserRepository
) {
    fun execute(input: PostCreateReqDto, owner: Long): Result<Long> {
        return runCatching {
            val authorizationPolicy = PostAuthorizationPolicy()
            val ownerUser: User = userRepository.getById(owner).getOrThrow()
            val uuid = Random.nextLong(until = 1_000)
            val post = Post(uuid, input.title, input.content, owner)
            post.isValid()
            if (!authorizationPolicy.isAuthorized(ownerUser, Action.CREATE, post)) {
                throw DomainExceptions.NotAllowedException("User has not allowed to create posts!")
            }
            val createdIdPost: Long = postRepository.create(post).getOrThrow()
            createdIdPost
        }.onFailure {err ->
            when(err) {
                is RepositoryExceptions.NotFoundException -> ApplicationException.NotFoundError(err.message)
                is DomainExceptions.ValidationException -> ApplicationException.ValidationError(err.errors)
                is DomainExceptions.NotAllowedException -> ApplicationException.NotAllowedError(err.message)
            }
        }
    }
}