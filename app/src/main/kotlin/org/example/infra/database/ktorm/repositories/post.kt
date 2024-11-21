package org.example.infra.database.ktorm.repositories

import org.example.infra.http.ApiError
import org.example.domain.posts.Post
import org.example.domain.posts.PostRepository
import org.example.infra.database.ktorm.PostDb
import org.example.infra.database.ktorm.Posts
import org.example.infra.database.ktorm.Users
import org.example.infra.database.sqlite.DatabaseSingleton
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class SQLitePostRepository : PostRepository {
    private val database = DatabaseSingleton.database

    override fun create(post: Post): Result<Long> {
        val user = database.sequenceOf(Users)
                .firstOrNull() { it.id eq post.owner }
        // this check can be useless since owner is found before in use-case
        if (user == null) return Result.failure(ApiError.NotFoundError("Owner with ID ${post.owner} was not found!"))

        val insertedPostId = database.sequenceOf(Posts).add(PostDb {
            id = post.id
            title = post.title
            content = post.content
            timestamp = post.timestamp
            likes = post.likes
            isPrivate = post.isPrivate
            owner = user
        })
        return Result.success(insertedPostId.toLong())
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        database.sequenceOf(Posts).toList().forEach{
            posts.add(Post(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    timestamp = it.timestamp,
                    likes = it.likes,
                    isPrivate = it.isPrivate,
                    owner = it.owner.id
            ))
        }
        return posts
    }

    override fun getById(id: Long): Result<Post> {
        return database.sequenceOf(Posts).find { it.id eq id }
                ?.let {
                    val post = Post(
                            id = it.id,
                            title = it.title,
                            content = it.content,
                            timestamp = it.timestamp,
                            likes = it.likes,
                            isPrivate = it.isPrivate,
                            owner = it.owner.id
                    )
                    Result.success(post)
                }
                ?: Result.failure(ApiError.NotFoundError("Post with ID $id was not found"))
    }

    override fun delete(id: Long): Result<Boolean> {
        return database.sequenceOf(Posts).find { it.id eq id }
                ?.let {
                    it.delete()
                    Result.success(true)
                }
                ?: Result.failure(ApiError.NotFoundError("Post with ID $id was not found"))
    }

    override fun update(id: Long, updatedPost: Post): Result<Long> {
        return database.sequenceOf(Posts).find { it.id eq id }
                ?.apply {
                    updatedPost.title.let { this.title = it }
                    updatedPost.content.let { this.content = it }
                    updatedPost.likes.let { this.likes = it }
                    updatedPost.isPrivate.let { this.isPrivate = it }
                }?.let {
                    Result.success(it.id)
                } ?: Result.failure(ApiError.NotFoundError("Post with ID $id was not found"))
    }
}