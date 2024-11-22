package org.example.domain.posts

interface PostRepository {
    fun create(post: Post): Result<Long>
    fun getAll(): List<Post>
    fun getAllByOwner(owner: Long): List<Post>
    fun getById(id: Long): Result<Post>
    fun delete(id: Long): Result<Boolean>
    fun update(id: Long, updatedPost: Post): Result<Long>
}