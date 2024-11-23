package org.example.infra.http;

import com.google.gson.GsonBuilder
import io.javalin.json.JsonMapper
import java.lang.reflect.Type

class JsonMapper {
    private val gson = GsonBuilder().create()

    val gsonMapper = object : JsonMapper {
        override fun <T : Any> fromJsonString(json: String, targetType: Type): T =
            gson.fromJson(json, targetType)

        override fun toJsonString(obj: Any, type: Type): String =
            gson.toJson(obj, type)
    }
}