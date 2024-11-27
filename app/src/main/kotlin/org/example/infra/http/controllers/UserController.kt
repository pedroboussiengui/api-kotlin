package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.adapter.*
import org.example.application.Container
import org.example.application.usecases.user.*
import org.example.infra.bcrypt.BCryptPasswordHasher
import org.example.infra.database.ktorm.repositories.SQLiteUserRepository
import org.example.infra.filestorage.MinioFileHandler
import org.example.infra.filestorage.MinioSingletonConnection
import org.example.infra.http.HttpStatus
import org.example.infra.http.controllers.ContextHelpers.handleError
import org.example.infra.http.controllers.ContextHelpers.handleException
import org.example.infra.http.controllers.ContextHelpers.validId
import org.example.infra.redis.RedisInMemoryUserDAO

object UserController {
    private val minioClient = MinioSingletonConnection.minioClient

    fun add(ctx: Context) {
        val req = ctx.bodyAsClass(UserCreateReqDto::class.java)
        val addUserUseCase = AddUserUseCase(SQLiteUserRepository(), BCryptPasswordHasher())

        when(val res = addUserUseCase.execute(req)) {
            is Container.Success -> {
                ctx.status(201).json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun getAll(ctx: Context) {
        val getAllUsersUserUseCase = GetAllUsersUseCase(SQLiteUserRepository())

        when(val res = getAllUsersUserUseCase.execute()) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun getById(ctx: Context) {
        val id = ctx.validId() ?: return
        val getUserByIdUseCase = GetUserByIdUseCase(SQLiteUserRepository())

        when(val res = getUserByIdUseCase.execute(id)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun update(ctx: Context) {
        val id = ctx.validId() ?: return
        val req = ctx.bodyAsClass(UserUpdateReqDto::class.java)

        val updateUserUseCase = UpdateUserUseCase(SQLiteUserRepository())
        when (val res = updateUserUseCase.execute(id, req)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun remove(ctx: Context) {
        val id = ctx.validId() ?: return

        val removeUserByIdUseCase = RemoveUserByIdUseCase(SQLiteUserRepository())
        when (val res = removeUserByIdUseCase.execute(id)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun setAddress(ctx: Context) {
        val id = ctx.validId() ?: return
        val req = ctx.bodyAsClass(UserAddressReqDto::class.java)

        val setAddressUserUseCase = SetAddressUserUseCase(SQLiteUserRepository())
        when (val res = setAddressUserUseCase.execute(id, req)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun addModerator(ctx: Context) {
        val req = ctx.bodyAsClass(ModUserCreateReqDto::class.java)
        val addModeratorUserUseCase = AddModeratorUserUseCase(SQLiteUserRepository())

        when(val res = addModeratorUserUseCase.execute(req)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun uploadAvatar(ctx: Context) {
        val sessionCookie = ctx.cookie("session_id")
        if (sessionCookie == null) {
            ctx.handleError(HttpStatus.UNAUTHORIZED, "Authentication failed")
            return
        }

        val file = ctx.uploadedFile("avatar")
        if (file == null) {
            ctx.handleError(HttpStatus.BAD_REQUEST, "Upload file should not be null")
            return
        }

        val addUserAvatarUseCase = AddUserAvatarUseCase(
                SQLiteUserRepository(),
                MinioFileHandler(minioClient, "teste"),
                RedisInMemoryUserDAO()
        )
        val req = FileReqDto(file.content(), file.size(), file.extension())
        when (val res = addUserAvatarUseCase.execute(sessionCookie, req)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun getMe(ctx: Context) {
        val sessionCookie = ctx.cookie("session_id")
        if (sessionCookie == null) {
            ctx.handleError(HttpStatus.UNAUTHORIZED, "Authentication failed")
            return
        }

        val getMeUseCase = GetMeUseCase(SQLiteUserRepository(), RedisInMemoryUserDAO())
        when (val res = getMeUseCase.execute(sessionCookie)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }
}