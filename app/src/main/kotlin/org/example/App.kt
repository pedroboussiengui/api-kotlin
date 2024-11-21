package org.example

import org.example.infra.http.init

fun main() {
    init()

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
