package org.example

import org.example.posts.Post
import org.example.posts.PostRepository1
import org.example.users.Address
import org.example.users.User
import org.example.users.UserRepository
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*

interface UserDb : Entity<UserDb> {
    companion object : Entity.Factory<UserDb>()

    val id: Long
    val username: String
    val password: String
    val email: String
    val cep: String
    val rua: String
    val numero: Int
    val bairro: String
    val cidade: String
    val estado: String
}

object Users : Table<UserDb>("users_tb") {
    val id = long("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val password = varchar("password").bindTo { it.password }
    val email = varchar("email").bindTo { it.email }
    val cep = varchar("address_cep").bindTo { it.cep }
    val rua = varchar("address_rua").bindTo { it.rua }
    val numero = int("address_numero").bindTo { it.numero }
    val bairro = varchar("address_bairro").bindTo { it.bairro }
    val cidade = varchar("address_cidade").bindTo { it.cidade }
    val estado = varchar("address_estado").bindTo { it.estado }
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
                address = Address(
                        cep = it[Users.cep] ?: "",
                        rua = it[Users.rua] ?: "",
                        numero = it[Users.numero] ?: 0,
                        bairro = it[Users.bairro] ?: "",
                        cidade = it[Users.cidade] ?: "",
                        estado = it[Users.estado] ?: ""
                )
            )
            users.add(user)
        }
        return users
    }

    override fun getByEmail(email: String): User? {
        return database.from(Users).select().where { Users.email eq email }
            .map {
                User(
                    id = it[Users.id] ?: 0L,
                    username = it[Users.username] ?: "",
                    password = it[Users.password] ?: "",
                    email = it[Users.email] ?: "",
                    address = Address(
                            cep = it[Users.cep] ?: "",
                            rua = it[Users.rua] ?: "",
                            numero = it[Users.numero] ?: 0,
                            bairro = it[Users.bairro] ?: "",
                            cidade = it[Users.cidade] ?: "",
                            estado = it[Users.estado] ?: ""
                    )
                )
            }.singleOrNull()
    }

    override fun getById(id: Long): Result<User> {
        database.from(Users).select().where { Users.id eq id }
            .map {
                User(
                    id = it[Users.id] ?: 0L,
                    username = it[Users.username] ?: "",
                    password = it[Users.password] ?: "",
                    email = it[Users.email] ?: "",
                    address = Address(
                            cep = it[Users.cep] ?: "",
                            rua = it[Users.rua] ?: "",
                            numero = it[Users.numero] ?: 0,
                            bairro = it[Users.bairro] ?: "",
                            cidade = it[Users.cidade] ?: "",
                            estado = it[Users.estado] ?: ""
                    )
                )
            }.singleOrNull()
                ?.let { return Result.success(it) }
                ?: return Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
    }

    override fun update(id: Long, newData: UserUpdateReqDto): Result<User> {
//        val rowsUpdated = database.update(Users) {
//            newData.username?.let { set(Users.username, it) }
//            newData.password?.let { set(Users.password, it) }
//            newData.email?.let { set(Users.email, it) }
//            where { Users.id eq id }
//        }
        TODO("Not yet implemented")
    }

    override fun remove(id: Long): Result<Boolean> {
        val userExists = database.from(Users)
                .select(Users.id)
                .where { Users.id eq id }
                .map { it[Users.id] }
                .isNotEmpty()
        return if (userExists) {
            database.delete(Users) { it.id eq id }
            Result.success(true)
        } else {
            Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
        }
    }

    override fun setAddress(id: Long, address: Address): Result<User> {
        TODO("Not yet implemented")
    }
}

interface PostDb : Entity<PostDb> {
    companion object : Entity.Factory<PostDb>()

    val id: Long
    var title: String
    var content: String
    var timestamp: String
    var likes: Int
    var isPrivate: Boolean
    var owner: UserDb
}

object Posts : Table<PostDb>("posts_tb") {
    val id = long("id").primaryKey().bindTo { it.id }
    val title = varchar("title").bindTo { it.title }
    val content = text("content").bindTo { it.content }
    val timestamp = varchar("timestamp").bindTo { it.timestamp }
    val likes = int("likes").bindTo { it.likes }
    val isPrivate = boolean("is_private").bindTo { it.isPrivate }
    val owner = long("user_id").references(Users) { it.owner }.bindTo { it.id }
}

class SQLitePostRepository : PostRepository1 {
    private val database = initDatabase()

    override fun create(post: Post): Result<Long> {
        val user = database.sequenceOf(Users)
                .firstOrNull() { it.id eq post.owner }
        // this check can be useless since owner is found before in use-case
        if (user == null) return Result.failure(ApiError.NotFoundError("Owner with ID ${post.owner} was not found!"))

        val insertedPostId = database.sequenceOf(Posts).add(PostDb {
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