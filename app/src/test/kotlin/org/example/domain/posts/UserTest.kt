package org.example.domain.posts

import org.example.domain.users.User
import org.example.domain.users.UserType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun test_instantiateUser_success() {
        val user = User(1, "Pedro", "12345678", "pedro@gmail.com", UserType.USER, null)
        val result = user.isValid()
        assertTrue(result.isSuccess)
    }
}