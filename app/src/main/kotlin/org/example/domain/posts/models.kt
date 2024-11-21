package org.example.domain.posts

import org.example.ApiError
import org.example.domain.users.User
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.*
import org.valiktor.i18n.mapToMessage
import org.valiktor.validate
import java.time.LocalDateTime
import java.util.*

class Post(
    var title: String,
    var content: String,
    var owner: Long
) {
    var id: Long? = null    // post is created without id, it's defined in database
    var timestamp: String = LocalDateTime.now().toString()  // timestamp when it is created
    var likes: Int = 0      // like number is 0 obviously
    var isPrivate: Boolean = false // is not private by default

    // all args constructor
    constructor(
            id: Long,
            title: String,
            content: String,
            timestamp: String,
            likes: Int,
            isPrivate: Boolean,
            owner: Long
    ) : this(title, content, owner) {
        this.id = id
        this.timestamp = timestamp
        this.likes = likes
        this.isPrivate = isPrivate
    }

    fun isValid(): Result<Boolean> {
        try {
            validate(this) {
                validate(Post::title).hasSize(min = 3, max = 80)
                validate(Post::content).isNotBlank()
                validate(Post::timestamp).isNotNull()
                validate(Post::likes).isPositiveOrZero()
                validate(Post::isPrivate).isNotNull()
                validate(Post::owner).isNotNull() // post cannot be without user owner
            }
            return Result.success(true)
        } catch (ex: ConstraintViolationException) {
            val listErrs = ex.constraintViolations
                    .mapToMessage(baseName = "messages", locale = Locale.ENGLISH)
                    .map { "${it.property}: ${it.message}" }
                    .toList()
            return Result.failure(ApiError.ValidationError(listErrs))
        }
    }

    fun likePost() = likes++

    fun toggleIsPrivate() {
        isPrivate = !isPrivate
    }
}
