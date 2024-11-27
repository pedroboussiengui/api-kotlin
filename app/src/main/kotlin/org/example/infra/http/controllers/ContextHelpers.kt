package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.domain.DomainExceptions
import org.example.domain.RepositoryExceptions
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

    fun handleException(ctx: Context, ex: Throwable) {
        when (ex) {
            is DomainExceptions.ValidationException -> {
                ctx.handleError(HttpStatus.BAD_REQUEST, message = ex.message, details = ex.errors)
            }
            is DomainExceptions.ConflictException -> {
                ctx.handleError(HttpStatus.CONFLICT, message = ex.message)
            }
            is DomainExceptions.LimitExceededException -> {
                ctx.handleError(HttpStatus.BAD_REQUEST, message = ex.message)
            }
            is RepositoryExceptions.NotFoundException -> {
                ctx.handleError(HttpStatus.NOT_FOUND, message = ex.message)
            }
            is DomainExceptions.NotAllowedException -> {
                ctx.handleError(HttpStatus.FORBIDDEN, message = ex.message)
            }
            is DomainExceptions.NotAuthenticatedException -> {
                ctx.handleError(HttpStatus.UNAUTHORIZED, message = ex.message)
            }
            else -> ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, message = "Unknown error: ${ex.message}")
        }
    }
}