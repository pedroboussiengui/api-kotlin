package org.example.infra.redis

import org.example.infra.environments.Environment
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
}