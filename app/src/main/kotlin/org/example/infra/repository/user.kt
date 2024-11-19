package org.example.infra.repository

import org.example.ApiError
import org.example.domain.users.Address
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.infra.*
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class SQLiteUserRepository : UserRepository {
    private val database = initDatabase()

    override fun addUser(user: User): Result<Long> {
        val insertedUserId = database.sequenceOf(Users).add(fromDomain(user))
        return Result.success(insertedUserId.toLong())
    }

    override fun getAll(): List<User> {
        val users = mutableListOf<User>()
        database.sequenceOf(Users).toList().forEach{
            users.add(fromPersistence(it))
        }
        return users
    }

    override fun getByEmail(email: String): User? {
        database.sequenceOf(Users).find { it.email eq email }
                ?.let {
                    return fromPersistence(it)
                }
                ?: return null
    }

    override fun existsByEmail(email: String): Boolean {
        return database.sequenceOf(Users).any {it.email eq email}
    }

    override fun getById(id: Long): Result<User> {
        return database.sequenceOf(Users).find { it.id eq id }
                ?.let {
                    Result.success(fromPersistence(it))
                }
                ?: Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
    }

    override fun update(id: Long, newUser: User): Result<Long> {
        return database.sequenceOf(Users).find { it.id eq id }
                ?.apply {
                    newUser.username.let { this.username = it }
                    newUser.email.let { this.email = it }
                    this.flushChanges()
                }?.let {
                    Result.success(it.id)
                } ?: Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
    }

    override fun remove(id: Long): Result<Boolean> {
        return database.sequenceOf(Users).find { it.id eq id }
                ?.let {
                    it.delete()
                    Result.success(true)
                }
                ?: Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
    }

    override fun setAddress(id: Long, address: Address): Result<User> {
        return database.sequenceOf(Users).find { it.id eq id }
                ?.apply {
                    this.cep = address.cep
                    this.rua = address.rua
                    this.numero = address.numero
                    this.bairro = address.bairro
                    this.cidade = address.cidade
                    this.estado = address.estado
                    this.flushChanges()
                }?.let {
                    val user = fromPersistence(it)
                    Result.success(user)
                } ?: Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
    }

    private fun fromPersistence(userDb: UserDb) : User {
        return User(
                id = userDb.id,
                username = userDb.username,
                password = userDb.password,
                email = userDb.email,
                address = if (!isAddressNull(userDb)) {
                    Address(
                            cep = userDb.cep!!,
                            rua = userDb.rua!!,
                            numero = userDb.numero!!,
                            bairro = userDb.bairro!!,
                            cidade = userDb.cidade!!,
                            estado = userDb.estado!!
                    )
                } else null
        )
    }

    private fun isAddressNull(userDb: UserDb): Boolean {
        return userDb.cep == null &&
                userDb.rua == null &&
                userDb.numero == null &&
                userDb.bairro == null &&
                userDb.cidade == null &&
                userDb.estado == null
    }

    private fun fromDomain(user: User) : UserDb {
        return UserDb {
            id = user.id
            username = user.username
            password = user.password
            email = user.email
            cep = user.address?.cep
            rua = user.address?.rua
            numero = user.address?.numero
            bairro = user.address?.bairro
            cidade = user.address?.cidade
            estado = user.address?.estado
        }
    }
}