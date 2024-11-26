package org.example.application.usecases.post

import org.example.application.ApplicationException
import org.example.domain.DomainExceptions
import org.example.domain.RepositoryExceptions
import org.example.domain.posts.Post
import org.example.domain.posts.PostRepository

class GetPostsByUserUseCase(
        private val postRepository: PostRepository
) {
    fun execute(owner: Long): Result<List<Post>> {
        return runCatching {
            val posts = postRepository.getAllByOwner(owner)
            posts
        }.onFailure {err ->
            when(err) {
                is RepositoryExceptions.NotFoundException -> ApplicationException.NotFoundError(err.message)
                is DomainExceptions.ValidationException -> ApplicationException.ValidationError(err.errors)
                is DomainExceptions.NotAllowedException -> ApplicationException.NotAllowedError(err.message)
            }
        }
    }
}