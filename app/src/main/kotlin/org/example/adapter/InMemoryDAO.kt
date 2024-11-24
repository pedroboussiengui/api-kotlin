package org.example.adapter

/**
 * Essa interface representa o contrato de como um dado qualquer dever salvo in memmory
 */
interface InMemoryDAO<T> {
    fun save(key: String, value: T)
    fun get(key: String): T
    fun delete(key: String)
}