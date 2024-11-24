package org.example.infra.redis

import org.example.infra.environments.Environment
import org.example.infra.http.controllers.Framework
import redis.clients.jedis.Jedis

object JedisSingletonConn {
    private val env: Environment = Environment()
    private val host = env.get("jedis.host") as String
    private val port = env.get("jedis.port") as Int

    // TODO: implement connection pool
    // TODO: auth
    // TODO: health check

    val jedis: Jedis by lazy {
        Jedis(host, port)
    }

    fun healthcheck(): Pair<Boolean, String?> {
        return try {
            val res = jedis.ping()
            if (res == "PONG") {
                true to null
            } else {
                false to "Failed to get PING/PONG response from redis: $res"
            }
        } catch (e: Exception) {
            false to "Failed to connect to redis: ${e.message}"
        }
    }
}

data class RedisFramework(
        override val name: String = "Redis",
        override val status: String,
        override val message: String?
): Framework(name, status, message)