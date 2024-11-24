package org.example.infra.environments

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.InputStream

/**
 * class configurada para buscar um arquivo env.yaml ou env.yml e obter os valores a partir
 * da chave no tipo string, retorna null caso não encontre
 */
class Environment {
    private val configData: Map<String, Any>
    private val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("env.yml")
            ?: this::class.java.classLoader.getResourceAsStream("env.yml")
            ?: throw IllegalArgumentException("Env.yaml file not found")

    init {
        val yamlMapper = ObjectMapper(YAMLFactory())
        configData = yamlMapper.readValue(inputStream, Map::class.java) as Map<String, Any>
    }

    fun get(key: String): String {
        val keys = key.split(".")
        var currentValue: Any? = configData
        for (k in keys) {
            currentValue = (currentValue as? Map<*, *>)?.get(k)
            if (currentValue == null) {
                throw NoSuchElementException("Key '$key' not found in env file.")
            }
        }
        return resolveEnvVariables(currentValue.toString())
    }
}

private fun resolveEnvVariables(value: String): String {
    val regex = "^\\{(.*)}$".toRegex()
    val parts = value.split("?:")

    return if (parts.size == 2) {
        val envVariable = parts[0]
        val defaultValue = parts[1]
        if (regex.matches(envVariable)) {
            val env = envVariable.replace("^\\{(.*)}$".toRegex(), "$1")
            val envValue = System.getenv(env) ?: defaultValue
            envValue
        } else {
            value
        }
    } else if (parts.size == 1) {
        val envVariable = parts[0]
        if (regex.matches(envVariable)) {
            val env = envVariable.replace("^\\{(.*)}$".toRegex(), "$1")
            val envValue = System.getenv(env) ?: throw NoSuchElementException("Environment variable '$env' not found.")
            envValue
        } else {
            value
        }
    } else {
        throw IllegalArgumentException("Invalid format for value: $value")
    }
}