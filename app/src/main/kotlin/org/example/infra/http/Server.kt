package org.example.infra.http

import io.javalin.Javalin
import org.example.infra.environments.Environment
import org.example.infra.http.controllers.AuthenticationController
import org.example.infra.http.controllers.MonitoringController
import org.example.infra.http.controllers.PostController
import org.example.infra.http.controllers.UserController

class Server {
    private val mapper: JsonMapper = JsonMapper()
    private val env: Environment = Environment()
    private lateinit var app: Javalin // propriedade app será inicializada mais tarde

    init {
        setup()
        routing()
    }

    // config.cookies.secureOnly = true // Torna todos os cookies `Secure`
    private fun setup() {
        app = Javalin.create { config ->
            config.jsonMapper(mapper.gsonMapper)
        }
    }

    private fun routing() {
        // monitoramento
        app.get("/health", MonitoringController::healthcheck)

        app.post("/password-auth", AuthenticationController::authenticateByPassword)

        app.get("/me", UserController::getMe)

        app.get("/users", UserController::getAll)

        app.post("/users", UserController::add)

        app.post("/users/avatar", UserController::uploadAvatar)

        app.post("/users/as-moderator", UserController::addModerator)

        app.get("/users/{id}", UserController::getById)

        app.patch("/users/{id}", UserController::update)

        app.delete("/users/{id}", UserController::remove)

        app.post("/users/{id}/address", UserController::setAddress)

        app.post("/posts", PostController::create)

        app.get("/posts/my", PostController::getMyPosts)

        app.get("/posts/users/{id}", PostController::getUserPosts)
    }

    fun start() {
        app.start(env.get("server.port") as Int)
    }
}