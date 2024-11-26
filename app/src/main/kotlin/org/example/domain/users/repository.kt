package org.example.domain.users

import org.example.domain.RepositoryExceptions

interface UserRepository {
    fun addUser(user: User): Result<Long>
    fun getAll(): List<User>
    fun getByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun getById(id: Long): Result<User>
    fun update(id: Long, newUser: User): Result<Long>
    fun remove(id: Long): Result<Boolean>
    fun setAddress(id: Long, address: Address): Result<User>
    fun setAvatar(id: Long, avatarUrl: String): Result<User>
}