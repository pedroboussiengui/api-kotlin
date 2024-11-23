package org.example.infra.http

import io.javalin.Javalin
import org.example.infra.environments.Environment
import org.example.infra.http.controllers.MonitoringController
import org.example.infra.http.controllers.PostController
import org.example.infra.http.controllers.UserController

class Server {
    private val mapperConfig: MapperConfig = MapperConfig()
    private val env: Environment = Environment()
    private lateinit var app: Javalin // propriedade app será inicializada mais tarde

    init {
        setup()
    }

    private fun setup() {
        app = Javalin.create { config ->
            config.jsonMapper(mapperConfig.gsonMapper)
        }

        // monitoramento
        app.get("/health", MonitoringController::healthcheck)

        app.get("/users", UserController::getAll)

        app.post("/users", UserController::add)

        app.post("/users/avatar", UserController::uploadAvatar)

        app.post("/users/as-moderator", UserController::addModerator)
        ;
        app.get("/users/{id}", UserController::getById)

        app.patch("/users/{id}", UserController::update)

        app.delete("/users/{id}", UserController::remove)

        app.post("/users/{id}/address", UserController::setAddress)

        app.post("/posts", PostController::create)

        app.get("/posts/my", PostController::getMyPosts)

        app.get("/posts/users/{id}", PostController::getUserPosts)
    }

    fun start() {
        app.start(env.get("server.port") as? Int ?: 7070)
    }
}