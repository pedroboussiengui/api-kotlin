package org.example.application.adapter

import java.io.InputStream

/**
 * Essa inteface define um contrato de como deve ser a manipulação de arquivos da aplicação
 */
interface FileHandler {
    fun read(path: String): String?
    fun write(path: String, content: InputStream): String?
}