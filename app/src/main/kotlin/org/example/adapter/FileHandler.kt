package org.example.adapter

import java.io.InputStream

/**
 * Essa inteface define um contrato de como deve ser a manipulação de arquivos da aplicação
 */
interface FileHandler {
    fun download(path: String): String?
    fun upload(path: String, content: InputStream): String?
}