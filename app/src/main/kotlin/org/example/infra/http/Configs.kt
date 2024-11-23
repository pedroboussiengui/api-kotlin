package org.example.infra.http;

import com.google.gson.GsonBuilder
import io.javalin.json.JsonMapper
import java.lang.reflect.Type

class MapperConfig {
    private val gson = GsonBuilder().create()

    val gsonMapper = object : JsonMapper {
        override fun <T : Any> fromJsonString(json: String, targetType: Type): T =
            gson.fromJson(json, targetType)

        override fun toJsonString(obj: Any, type: Type): String =
            gson.toJson(obj, type)
    }
}

//// Cria um TypeAdapter para o Framework
//class FrameworkTypeAdapter : JsonSerializer<Framework>, JsonDeserializer<Framework> {
//    override fun serialize(src: Framework?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//        val jsonObject = JsonObject()
//        jsonObject.addProperty("name", src?.name)
//        jsonObject.addProperty("status", src?.status)
//        jsonObject.addProperty("message", src?.message)
//        return jsonObject
//    }
//
//    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Framework {
//        val jsonObject = json?.asJsonObject
//        val name = jsonObject?.get("name")?.asString
//        val status = jsonObject?.get("status")?.asString
//        val message = jsonObject?.get("message")?.asString
//        return Framework { name = name, status = status ?: "DOWN", message = message }
//    }
//}