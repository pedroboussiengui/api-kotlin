package org.example.domain

import org.example.domain.posts.Post
import org.example.domain.users.User
import org.example.domain.users.UserType
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PostAuthorizationPolicyTest {

    @Test
    fun test_isAuthorized_UserCreatePost_success() {
        val user = User(1, "Pedro", "12345678", "pedro@gmail.com", UserType.USER, null)
        val post = Post(1, "Meu post", "o conteúdo do meu post", 1)
        val authorizationPolicy = PostAuthorizationPolicy()
        val result = authorizationPolicy.isAuthorized(user, Action.CREATE, post)
        assertTrue(result)
    }

    @Test
    fun test_isAuthorized_UserCreatePost_failed() {
        val user = User(1, "Pedro", "12345678", "pedro@gmail.com", UserType.MODERATOR, null)
        val post = Post(1, "Meu post", "o conteúdo do meu post", 1)
        val authorizationPolicy = PostAuthorizationPolicy()
        val result = authorizationPolicy.isAuthorized(user, Action.CREATE, post)
        assertFalse(result)
    }

    @Test
    fun test_isAuthorized_UserViewPost() {
        val user = User(1, "Pedro", "12345678", "pedro@gmail.com", UserType.USER, null)
        val other = User(2, "Lucas", "87654321", "other@email.com", UserType.USER, null)
        // by default a post is public
        val post1 = Post(1, "Meu post", "o conteúdo do meu post", 1)
        val post2 = Post(2, "post do lucas", "o conteúdo do post do lucas", 2)
        val authorizationPolicy = PostAuthorizationPolicy()
        // user can view this own post
        val result1 = authorizationPolicy.isAuthorized(user, Action.VIEW, post1)
        assertTrue(result1)
        // user can view other post because it's public
        val result2 = authorizationPolicy.isAuthorized(user, Action.VIEW, post2)
        assertTrue(result2)
        // now the post2 is private
        post2.toggleIsPrivate()
        // lucas can see this own post yet because he is the owner
        val result3 = authorizationPolicy.isAuthorized(other, Action.VIEW, post2)
        assertTrue(result3)
        // use cannot view post new because it's toggle to private
        val result4 = authorizationPolicy.isAuthorized(user, Action.VIEW, post2)
        assertFalse(result4)
    }
}