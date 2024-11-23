package org.example.infra.http.controllers

import io.javalin.http.Context
import io.minio.MinioClient
import org.example.infra.http.HttpStatus
import org.example.application.UseCaseResult
import org.example.adapter.FileHandler
import org.example.application.usecases.*
import org.example.infra.database.ktorm.repositories.SQLiteUserRepository
import org.example.infra.filehandler.MinioFileHandler
import org.example.infra.http.controllers.ContextHelpers.contextUser
import org.example.infra.http.controllers.ContextHelpers.handleError
import org.example.infra.http.controllers.ContextHelpers.validId

object UserController {
    private val minioClient: MinioClient by lazy {
        MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build()
    }

    fun add(ctx: Context) {
        val req = ctx.bodyAsClass(UserCreateReqDto::class.java)
        val addUserUseCase = AddUserUseCase(SQLiteUserRepository())
        when (val res = addUserUseCase.execute(req)) {
            is UseCaseResult.Success -> {
                ctx.status(201).json(res.data)
            }
            is UseCaseResult.BusinessRuleError -> {
                ctx.handleError(HttpStatus.CONFLICT, message = res.message)
            }
            is UseCaseResult.ValidationError -> {
                ctx.handleError(HttpStatus.BAD_REQUEST, message = "Validation error", subErrors = res.errors)
            }
            else -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        }
    }

    fun getAll(ctx: Context) {
        val getAllUsersUserUseCase = GetAllUsersUseCase(SQLiteUserRepository())
        val res = getAllUsersUserUseCase.execute()
        ctx.json(res)
    }

    fun getById(ctx: Context) {
        val id = ctx.validId() ?: return
        val getUserByIdUseCase = GetUserByIdUseCase(SQLiteUserRepository())
        when (val res = getUserByIdUseCase.execute(id)) {
            is UseCaseResult.Success -> {
                ctx.json(res.data)
            }
            is UseCaseResult.NotFoundError -> {
                ctx.handleError(HttpStatus.NOT_FOUND, message = res.message)
            }
            else -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        }
    }

    fun update(ctx: Context) {
        val id = ctx.validId() ?: return
        val req = ctx.bodyAsClass(UserUpdateReqDto::class.java)

        val updateUserUseCase = UpdateUserUseCase(SQLiteUserRepository())
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
            else -> ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        }
    }

    fun remove(ctx: Context) {
        val id = ctx.validId() ?: return

        val removeUserUseCase = RemoveUseUseCase(SQLiteUserRepository())
        when (val res = removeUserUseCase.execute(id)) {
            is UseCaseResult.Success -> {
                ctx.json(res.data)
            }
            is UseCaseResult.NotFoundError -> {
                ctx.handleError(HttpStatus.NOT_FOUND, message = res.message)
            }
            else -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        }
    }

    fun setAddress(ctx: Context) {
        val id = ctx.validId() ?: return
        val req = ctx.bodyAsClass(UserAddressReqDto::class.java)

        val setAddressUserUseCase = SetAddressUserUseCase(SQLiteUserRepository())
        when (val res = setAddressUserUseCase.execute(id, req)) {
            is UseCaseResult.Success -> {
                ctx.json(res.data)
            }
            is UseCaseResult.NotFoundError -> {
                ctx.handleError(HttpStatus.NOT_FOUND, message = res.message)
            }
            is UseCaseResult.ValidationError -> {
                ctx.handleError(HttpStatus.BAD_REQUEST, message = "Validation error", subErrors = res.errors)
            }
            else -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        }
    }

    fun addModerator(ctx: Context) {
        val req = ctx.bodyAsClass(ModUserCreateReqDto::class.java)
        val addModeratorUserUseCase = AddModeratorUserUseCase(SQLiteUserRepository())
        when (val res = addModeratorUserUseCase.execute(req)) {
            is UseCaseResult.Success -> {
                ctx.status(201).json(res.data)
            }
            is UseCaseResult.BusinessRuleError -> {
                ctx.handleError(HttpStatus.CONFLICT, message = res.message)
            }
            is UseCaseResult.ValidationError -> {
                ctx.handleError(HttpStatus.BAD_REQUEST, message = "Validation error", subErrors = res.errors)
            }
            else -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        }
    }

    fun uploadAvatar(ctx: Context) {
        val file = ctx.uploadedFile("avatar") ?: return

        val contextUser = ctx.contextUser() ?: return

        val addUserAvatarUseCase = AddUserAvatarUseCase(
                SQLiteUserRepository(),
                MinioFileHandler(minioClient, "teste")
        )
        val req = FileReqDto(file.content(), file.size(), file.extension())
        when (val res = addUserAvatarUseCase.execute(contextUser, req)) {
            is UseCaseResult.Success -> {
                ctx.json(res)
            }
            is UseCaseResult.BusinessRuleError -> {
                ctx.handleError(HttpStatus.BAD_REQUEST, res.message)
            }
            is UseCaseResult.InternalError -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, res.message)
            }
            is UseCaseResult.NotAllowedError -> {
                ctx.handleError(HttpStatus.FORBIDDEN, res.message)
            }
            is UseCaseResult.NotFoundError -> {
                ctx.handleError(HttpStatus.NOT_FOUND, res.message)
            }
            else -> ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        }

    }
}