package org.example.infra.redis

import org.example.adapter.InMemoryDAO
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

/**
 * Implementação concreta que usa o redis para criar um sessão para o user,
 * mapeando uma chave para o userId
 */
class RedisInMemoryUserDAO: InMemoryDAO<Long> {
    private val jedis = JedisSingletonConn.jedis
    // suponha que a duração da sessão é 1h
    private val expireIn: Long = 1.hours.toLong(DurationUnit.SECONDS)

    override fun save(key: String, value: Long) {
        jedis.setex(key, expireIn, value.toString())
    }

    override fun get(key: String): Long? {
        return jedis.get(key)?.toLong()
    }

    override fun delete(key: String) {
        jedis.del(key)
    }

    override fun getAll(): Set<String> {
        return jedis.keys("*")
    }

    override fun remainingTime(key: String): Long {
        return jedis.ttl(key)
    }
}