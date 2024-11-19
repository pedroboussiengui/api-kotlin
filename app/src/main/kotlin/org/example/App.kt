package org.example

import io.javalin.Javalin
import io.javalin.http.Context
import org.example.domain.posts.Post
import org.example.domain.posts.PostRepository
import org.example.domain.users.Address
import org.example.domain.users.User
import org.example.domain.users.UserRepository
import org.example.infra.repository.SQLitePostRepository
import org.example.infra.repository.SQLiteUserRepository
import kotlin.random.Random

class UserCreateReqDto(
    val username: String,
    val password: String,
    val email: String
)

class UserAddressSetReqDto(
    val cep: String,
    val rua: String,
    val numero: Int,
    val bairro: String,
    val cidade: String,
    val estado: String
)

class UserUpdateReqDto(
    val username: String?,
    val email: String?
)

class PostReqDto(
    val title: String,
    val content: String
)

fun main() {
    val mapperConfig = MapperConfig()
    val userDb: UserRepository = SQLiteUserRepository()
    val postDb: PostRepository = SQLitePostRepository()

    val app = Javalin.create { config ->
        config.jsonMapper(mapperConfig.gsonMapper)
    }.start(7070)

    // Helper para lidar com erros
    fun Context.handleError(httpStatus: HttpStatus, message: String, subErrors: Any? = null) {
        val apiError = ApiErrorResponse(
            status = httpStatus.code,
            message = message,
            path = this.path(),
            subErrors = subErrors
        )
        this.status(httpStatus.code).json(apiError)
    }

    // Helper para validação de ID
    fun Context.validId(): Long? {
        return runCatching { this.pathParam("id").toLong() }.getOrNull().also {
            if (it == null) {
                this.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "${HttpStatus.INTERNAL_SERVER_ERROR}: Invalid ID format")
            }
        }
    }

    fun Context.conextUser(): Long? {
        return this.header("User")?.toLongOrNull()
                ?: run {
                    this.handleError(HttpStatus.UNAUTHORIZED, "User is not authenticated or invalid")
                    return null
                }
    }

    app.get("/users") { ctx ->
        val users = userDb.getAll()
        ctx.json(users)
    }

    app.get("/users/{id}") { ctx ->
        val id = ctx.validId() ?: return@get
        userDb.getById(id).fold(
            onFailure = { err ->
                if (err is ApiError.NotFoundError) ctx.handleError(HttpStatus.NOT_FOUND, err.message)
            },
            onSuccess = { user -> ctx.json(user) }
        )
    }

    app.post("/users/{id}/address") { ctx ->
        val id = ctx.validId() ?: return@post
        val req = ctx.bodyAsClass(Address::class.java)

        userDb.setAddress(id, req).fold(
            onFailure = { err ->
                if (err is ApiError.NotFoundError) ctx.handleError(HttpStatus.NOT_FOUND, err.message)
                return@post
            },
            onSuccess = { updatedUser -> updatedUser.isValid().fold(
                onFailure = { err ->
                    if (err is ApiError.ValidationError) {
                        ctx.handleError(HttpStatus.BAD_REQUEST, "Validation error", err.errors)
                        return@post
                    }
                    ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: ${err.message}")
                    return@post
                },
                onSuccess = { ctx.json(updatedUser) }
            ) }
        )
    }

    app.post("/users") { ctx ->
        val req = ctx.bodyAsClass(UserCreateReqDto::class.java)
        val uuid = Random.nextLong(until = 1_000)
        val user = User(uuid, req.username, req.password, req.email, null)

        user.isValid().fold(
            onFailure = { err ->
                if (err is ApiError.ValidationError) {
                    ctx.handleError(HttpStatus.BAD_REQUEST, "Validation error", err.errors)
                    return@post
                }
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: ${err.message}")
                return@post
            },
            onSuccess = { /* User is valid, continue */ }
        )

        if (userDb.getByEmail(user.email) != null) {
            ctx.handleError(HttpStatus.CONFLICT, "Business rule error: E-mail already exists")
            return@post
        }

        userDb.addUser(user)
        ctx.status(201).json(user)
    }

    app.patch("/users/{id}") { ctx ->
        val id = ctx.validId() ?: return@patch
        val req = ctx.bodyAsClass(UserUpdateReqDto::class.java)
        // busca usuario pelo id, garantindo que ele existe
        val user: User = userDb.getById(id).getOrElse { err ->
            if (err is ApiError.NotFoundError) ctx.handleError(HttpStatus.NOT_FOUND, err.message)
            return@patch
        }
        // atribuo os campos e não forem null
        req.username?.let { user.username = it }
        req.email?.let { user.email = it }

        // email não pode existir
        if (userDb.existsByEmail(user.email)) {
            ctx.handleError(HttpStatus.CONFLICT, "Business rule error: E-mail already exists")
            return@patch
        }

        // valido o user com os dados novos
        user.isValid().onFailure { err ->
            if (err is ApiError.ValidationError) {
                ctx.handleError(HttpStatus.BAD_REQUEST, "Validation error", err.errors)
                return@patch
            }
        }
        // caso sucesso, ele atualiza com os dados existentes
        val updatedUserId: Long = userDb.update(id, user).getOrElse { err ->
            if (err is ApiError.NotFoundError) ctx.handleError(HttpStatus.NOT_FOUND, err.message)
            return@patch
        }
        ctx.json(updatedUserId)
    }

    app.delete("/users/{id}") { ctx ->
        val id = ctx.validId() ?: return@delete

        userDb.remove(id).fold(
            onFailure = { err ->
                if (err is ApiError.NotFoundError) ctx.handleError(HttpStatus.NOT_FOUND, err.message)
                return@delete
            },
            onSuccess = { ctx.status(204) }
        )
    }

    app.post("/posts") { ctx ->
        // get the user id from the header
        val owner = ctx.conextUser() ?: return@post
        // get dto as body request
        val req = ctx.bodyAsClass(PostReqDto::class.java)
        // create a new post for tht user
        val post = Post(req.title, req.content, owner)
        // valid the post
        post.isValid().onFailure { err ->
            if (err is ApiError.ValidationError) {
                ctx.handleError(HttpStatus.BAD_REQUEST, "Validation error", err.errors)
                return@post
            }
        }
        // persist in db
        val createdPostId = postDb.create(post).getOrElse { err ->
            if (err is ApiError.NotFoundError) {
                ctx.handleError(HttpStatus.NOT_FOUND, err.message)
                return@post
            }
        }
        // response to client
        ctx.json(createdPostId)
    }
}
