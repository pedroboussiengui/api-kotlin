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
            ?: throw IllegalArgumentException("Arquivo 'env.yml' não encontrado no classpath")

    init {
        val yamlMapper: ObjectMapper = ObjectMapper(YAMLFactory())
        configData = yamlMapper.readValue(inputStream, Map::class.java) as Map<String, Any>
    }

    fun get(key: String): Any? {
        val keys = key.split(".")
        var currentValue: Any? = configData
        for (k in keys) {
            currentValue = (currentValue as? Map<*, *>)?.get(k)
            if (currentValue == null) return null
        }
        return currentValue
    }
}