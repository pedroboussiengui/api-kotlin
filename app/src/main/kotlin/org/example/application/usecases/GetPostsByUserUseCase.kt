package org.example.application.usecases

import org.example.domain.posts.Post
import org.example.domain.posts.PostRepository

class GetPostsByUserUseCase(
        private val postRepository: PostRepository
) {
    fun execute(owner: Long): List<Post> {
        return postRepository.getAllByOwner(owner)
    }
}