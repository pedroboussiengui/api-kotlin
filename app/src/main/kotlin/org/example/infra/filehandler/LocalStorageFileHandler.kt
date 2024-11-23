package org.example.infra.filehandler

import org.example.adapter.FileHandler
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Essa é a implementação concreta, que nesse caso salva no próprio sistema de arquivos da máquina,
 * mas poderia ser no S3 bucket da AWS, Azure Blob Storage (Microsoft), núvem privada com NextCloud,
 * Dropbox, Firebase, enfim, uma infinidade de opções, mas que nunca devem interferir no meu domínio
 */
//class LocalStorageFileHandler: FileHandler {
//    override fun read(path: String): String? {
//        return try {
//            val filePath = Paths.get(path)
//            if (Files.exists(filePath)) {
//                String(Files.readAllBytes(filePath))
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            println("Erro ao ler o arquivo: ${e.message}")
//            null
//        }
//    }
//
//    override fun write(path: String, content: ByteArray): String? {
//        return try {
//            val filePath = Paths.get(path)
//            Files.write(filePath, content)
//            filePath.toString()
//        } catch (e: Exception) {
//            println("Erro ao salvar o arquivo: ${e.message}")
//            null
//        }
//    }
//}