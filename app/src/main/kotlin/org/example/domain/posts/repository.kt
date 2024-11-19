package org.example.domain.posts

interface PostRepository {
    fun create(post: Post): Result<Long>
    fun getAll(): List<Post>
    fun getById(id: Long): Result<Post>
    fun delete(id: Long): Result<Boolean>
    fun update(id: Long, updatedPost: Post): Result<Long>
}

//interface PostRepository2 {
//    fun create(post: Post): Result<Long>
//    fun getAll(): List<Post>
//    fun getById(id: Long): Result<Post>
//    fun delete(id: Long): Result<Boolean>
//    fun changeTitle(id: Long, newTitle: String): Result<Post>
//    fun changeContent(id: Long, newContent: String): Result<Post>
//    fun likePost(id: Long): Result<Post>
//    fun toggleIsPrivate(id: Long): Result<Post>
//}