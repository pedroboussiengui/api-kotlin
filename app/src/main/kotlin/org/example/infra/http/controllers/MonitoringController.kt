package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.infra.filestorage.MinioFramework
import org.example.infra.filestorage.MinioSingletonConnection

abstract class Framework(
    open val name: String,
    open val status: String,
    open val message: String?
)

data class HealthResponse(
    var frameworks: List<Framework> = mutableListOf()
)

object MonitoringController {
    fun healthcheck(ctx: Context) {
        val res = HealthResponse()
        val health = MinioSingletonConnection.healthcheck()
        val minioFramework = MinioFramework(
                name = "MinIO object Storage",
                status = if (health.first) "UP" else "DOWN",
                message = health.second
        )
        res.frameworks += minioFramework
        ctx.json(res)
    }
}