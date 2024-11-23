package org.example.infra.filestorage

import io.minio.MinioClient
import org.example.infra.http.controllers.Framework
import java.net.HttpURLConnection
import java.net.URL

/**
 * https://min.io/docs/minio/linux/operations/monitoring/healthcheck-probe.html
 */
object MinioSingletonConnection {
    private val endpoint = "http://localhost:9000"
    private val accessKey = "minioadmin"
    private val secretKey = "minioadmin"
    private val healthUrl = "http://localhost:9000/minio/health/live"

    val minioClient: MinioClient by lazy {
        MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build()
    }

    fun healthcheck(): Pair<Boolean, String?> {
        return try {
            val url = URL(healthUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 5 segundos
            Pair(connection.responseCode == 200, null)
        } catch (e: Exception) {
            return Pair(false, e.message)
        }
    }
}

data class MinioFramework(
        override val name: String = "MinIO object Storage",
        override val status: String,
        override val message: String?
): Framework(name, status, message)