package org.example.domain.posts

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PostTest {
    @Test
    fun test_instantiatePost_success() {
        val post = Post(1, "Meu post", "o conteúdo do meu post", 1)
        val result = post.isValid()
        assertTrue(result.isSuccess)
        assertNotNull(post.id)
        assertNotNull(post.timestamp)
        assertEquals(0, post.likes)
        assertFalse(post.isPrivate)
    }

    @Test
    fun test_instantiatePost_fail() {
        val post = Post(1, "Me", "", 1)
        val result = post.isValid()
        assertTrue(result.isFailure)
    }

    @Test
    fun test_likePost() {
        val post = Post(1, "Meu post", "o conteúdo do meu post", 1)
        assertEquals(0, post.likes)
        post.likePost()
        assertEquals(1, post.likes)
        post.likePost()
        assertEquals(2, post.likes)
    }

    @Test
    fun toggleIsPrivate() {
        val post = Post(1, "Meu post", "o conteúdo do meu post", 1)
        assertFalse(post.isPrivate)
        post.toggleIsPrivate()
        assertTrue(post.isPrivate)
        post.toggleIsPrivate()
        assertFalse(post.isPrivate)
    }
}