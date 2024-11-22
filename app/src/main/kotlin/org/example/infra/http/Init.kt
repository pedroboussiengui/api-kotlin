package org.example.infra.http

import io.javalin.Javalin
import org.example.infra.http.controllers.PostController
import org.example.infra.http.controllers.UserController

fun init() {
    val mapperConfig = MapperConfig()

    val app = Javalin.create { config ->
        config.jsonMapper(mapperConfig.gsonMapper)
    }

    app.get("/users", UserController::getAll)

    app.post("/users", UserController::add)

    app.post("/users/as-moderator", UserController::addModerator)

    app.get("/users/{id}", UserController::getById)

    app.patch("/users/{id}", UserController::update)

    app.delete("/users/{id}", UserController::remove)

    app.post("/users/{id}/address", UserController::setAddress)

    app.post("/posts", PostController::create)

    app.get("/posts/my", PostController::getMyPosts)

    app.get("/posts/users/{id}", PostController::getUserPosts)

    app.start(7070)
}