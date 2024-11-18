package org.example

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Users : Table<Nothing>("users_tb") {
    val id = long("id").primaryKey()
    val username = varchar("username")
    val password = varchar("password")
    val email = varchar("email")
}

fun initDatabase(): Database {
    return Database.connect("jdbc:sqlite:sample.db") // Caminho do arquivo SQLite
}

class SQLiteUserRepository : UserRepository {
    private val database = initDatabase()

    override fun addUser(user: User) {
        database.useTransaction {
            database.insert(Users) {
                set(it.id, user.id)
                set(it.username, user.username)
                set(it.password, user.password)
                set(it.email, user.email)
            }
        }
    }

    override fun getAll(): List<User> {
        val users = mutableListOf<User>()
        database.from(Users).select().forEach {
            val user = User(
                id = it[Users.id] ?: 0L,
                username = it[Users.username] ?: "",
                password = it[Users.password] ?: "",
                email = it[Users.email] ?: "",
                address = null
            )
            users.add(user)
        }
        return users
    }

    override fun getByEmail(email: String): User? {
        return database.from(Users).select()
            .where { Users.email eq email }
            .map {
                User(
                    id = it[Users.id] ?: 0L,
                    username = it[Users.username] ?: "",
                    password = "dasdasdadasdasd",
                    email = it[Users.email] ?: "",
                    address = null
                )
            }.singleOrNull()
    }

    override fun getById(id: Long): Result<User> {
        TODO("Not yet implemented")
    }

    override fun update(id: Long, newData: UserUpdateReqDto): Result<User> {
        TODO("Not yet implemented")
    }

    override fun remove(id: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setAddress(id: Long, address: Address): Result<User> {
        TODO("Not yet implemented")
    }

}
