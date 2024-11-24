package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.infra.filestorage.MinioFramework
import org.example.infra.filestorage.MinioSingletonConnection
import org.example.infra.redis.JedisSingletonConn
import org.example.infra.redis.RedisFramework
import org.example.infra.redis.RedisInMemoryUserDAO

abstract class Framework(
    open val name: String,
    open val status: String,
    open val message: String?
)

data class HealthResponse(
    var frameworks: List<Framework> = mutableListOf()
)

data class ActiveSessionResponse(
    var sessionId: String,
    var remainingTimeInSeconds: Long
)

object MonitoringController {

    fun getActiveSessions(ctx: Context) {
        val redis = RedisInMemoryUserDAO()
        val sessions = redis.getAll()
        if (sessions.isEmpty()) {
            ctx.status(204).result("No active sessions found")
            return
        }

        val res = mutableListOf<ActiveSessionResponse>()
        for (session in sessions) {
            val remainingTime = redis.remainingTime(session)
            val maskedSessionId = session.take(6) + "**********"
            res.add(ActiveSessionResponse(maskedSessionId, remainingTime))
        }
        ctx.json(res)
    }

    fun healthcheck(ctx: Context) {
        val res = HealthResponse()
        integrateMinIOFramework(res)
        integrateRedisFramework(res)
        ctx.json(res)
    }

    private fun integrateMinIOFramework(res: HealthResponse) {
        val health = MinioSingletonConnection.healthcheck()
        val minioFramework = MinioFramework(
                status = if (health.first) "UP" else "DOWN",
                message = health.second
        )
        res.frameworks += minioFramework
    }

    private fun integrateRedisFramework(res: HealthResponse) {
        val health = JedisSingletonConn.healthcheck()
        res.frameworks += RedisFramework(
                status = if (health.first) "UP" else "DOWN",
                message = health.second
        )
    }
}