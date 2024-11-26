package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.infra.http.ApiErrorResponse
import org.example.infra.http.HttpStatus

object ContextHelpers {

    // Helper para validação de ID
    fun Context.validId(): Long? {
        return runCatching { this.pathParam("id").toLong() }.getOrNull().also {
            if (it == null) {
                this.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "${HttpStatus.INTERNAL_SERVER_ERROR}: Invalid ID format")
            }
        }
    }

    // Helper para lidar com erros
    fun Context.handleError(httpStatus: HttpStatus, message: String, details: Any? = null) {
        val apiError = ApiErrorResponse(
                status = httpStatus.code,
                message = message,
                path = this.path(),
                details = details
        )
        this.status(httpStatus.code).json(apiError)
    }

    // Helper para recuperar usuário autenticado
    fun Context.contextUser(): Long? {
        return this.header("User")?.toLongOrNull()
                ?: run {
                    this.handleError(HttpStatus.UNAUTHORIZED, "User is not authenticated or invalid")
                    null
                }
    }
}