package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.ApiErrorResponse
import org.example.HttpStatus
import org.example.application.usecases.GetAllUsersUseCase
import org.example.application.usecases.UpdateUserUseCase
import org.example.application.usecases.UseCaseResult
import org.example.application.usecases.UserUpdateReqDto

object UserController {

    // Helper para validação de ID
    private fun Context.validId(): Long? {
        return runCatching { this.pathParam("id").toLong() }.getOrNull().also {
            if (it == null) {
                this.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "${HttpStatus.INTERNAL_SERVER_ERROR}: Invalid ID format")
            }
        }
    }

    // Helper para lidar com erros
    private fun Context.handleError(httpStatus: HttpStatus, message: String, subErrors: Any? = null) {
        val apiError = ApiErrorResponse(
                status = httpStatus.code,
                message = message,
                path = this.path(),
                subErrors = subErrors
        )
        this.status(httpStatus.code).json(apiError)
    }

    fun update(ctx: Context) {
        val id = ctx.validId() ?: return
        val req = ctx.bodyAsClass(UserUpdateReqDto::class.java)

        val updateUserUseCase = UpdateUserUseCase()
        when (val res = updateUserUseCase.execute(id, req)) {
            is UseCaseResult.Success -> {
                ctx.json(res.data)
            }
            is UseCaseResult.BusinessRuleError -> {
                ctx.handleError(HttpStatus.CONFLICT, message = res.message)
            }
            is UseCaseResult.NotFoundError -> {
                ctx.handleError(HttpStatus.NOT_FOUND, message = res.message)
            }
            is UseCaseResult.ValidationError -> {
                ctx.handleError(HttpStatus.BAD_REQUEST, message = "Validation error", subErrors = res.errors)
            }
        }
    }

    fun getAll(ctx: Context) {
        val getAllUsersUserUseCase = GetAllUsersUseCase()
        val res = getAllUsersUserUseCase.execute()
        ctx.json(res)
    }

    fun getById(ctx: Context) {
        //TODO
    }

    fun remove(ctx: Context) {
        //TODO
    }

    fun setAddress(ctx: Context) {
        //TODO
    }
}