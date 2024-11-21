package org.example

import io.javalin.Javalin
import org.example.infra.http.controllers.UserController

fun main() {
    val mapperConfig = MapperConfig()

    val app = Javalin.create { config ->
        config.jsonMapper(mapperConfig.gsonMapper)
    }.start(7070)

    app.get("/users", UserController::getAll)

    app.post("/users", UserController::add)

    app.get("/users/{id}", UserController::getById)

    app.patch("/users/{id}", UserController::update)

    app.delete("/users/{id}", UserController::remove)

    app.post("/users/{id}/address", UserController::setAddress)

//    app.post("/posts") { ctx ->
//        // get the user id from the header
//        val owner = ctx.conextUser() ?: return@post
//        // get dto as body request
//        val req = ctx.bodyAsClass(PostReqDto::class.java)
//        // create a new post for tht user
//        val post = Post(req.title, req.content, owner)
//        // valid the post
//        post.isValid().onFailure { err ->
//            if (err is ApiError.ValidationError) {
//                ctx.handleError(HttpStatus.BAD_REQUEST, "Validation error", err.errors)
//                return@post
//            }
//        }
//        // persist in db
//        val createdPostId = postDb.create(post).getOrElse { err ->
//            if (err is ApiError.NotFoundError) {
//                ctx.handleError(HttpStatus.NOT_FOUND, err.message)
//                return@post
//            }
//        }
//        // response to client
//        ctx.json(createdPostId)
//    }
}
