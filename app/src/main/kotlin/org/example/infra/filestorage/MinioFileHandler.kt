package org.example.infra.filestorage

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.errors.MinioException
import io.minio.http.Method
import org.example.adapter.FileHandler
import java.io.InputStream

/**
 * Essa é a implementação concreta, que nesse caso salva no local storage minio,
 * mas poderia ser no S3 bucket da AWS, Azure Blob Storage (Microsoft), núvem privada com NextCloud,
 * Dropbox, Firebase, enfim, uma infinidade de opções, mas que nunca devem interferir no meu domínio
 */
class MinioFileHandler(
        private val minioClient: MinioClient,
        private val bucketName: String
): FileHandler {
    override fun download(path: String): String? {
        return try {
            val url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .`object`(path)
                            .expiry(60 * 60)
                            .build()
            )
            url
        } catch (e: MinioException) {
            println("Erro ao gerar a URL pré-assinada: ${e.message}")
            return null
        }
    }

    override fun upload(path: String, content: InputStream): String? {
        return try {
            minioClient.putObject(
                    io.minio.PutObjectArgs.builder()
                            .bucket(bucketName)
                            .`object`(path)
                            .stream(content, content.available().toLong(), -1)
                            .contentType("application/octet-stream")
                            .build()
            )
            download(path)
        } catch (e: Exception) {
            println("Erro ao salvar o arquivo: ${e.message}")
            null
        }
    }

}