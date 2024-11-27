package org.example.application.usecases.post

import org.example.adapter.InMemoryDAO
import org.example.application.Container
import org.example.domain.posts.Post
import org.example.domain.posts.PostRepository

class GetMyPostsUseCase (
        private val postRepository: PostRepository,
        private val inMemoryDAO: InMemoryDAO<Long>
) {
    fun execute(sessionId: String): Container<Throwable, List<Post>> = Container.catch {
        val id: Long = inMemoryDAO.get(sessionId)
                ?: throw Exception("Invalid session")

        val posts = postRepository.getAllByOwner(id)
        posts
    }
}