package org.example.infra.filestorage

import io.minio.MinioClient

object MinioSingletonConnection {
    val minioClient: MinioClient by lazy {
        MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build()
    }
}