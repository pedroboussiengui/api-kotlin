package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.application.UseCaseResult
import org.example.application.usecases.CreatePostUseCase
import org.example.application.usecases.GetPostsByUserUseCase
import org.example.application.usecases.PostCreateReqDto
import org.example.infra.database.ktorm.repositories.SQLitePostRepository
import org.example.infra.database.ktorm.repositories.SQLiteUserRepository
import org.example.infra.http.HttpStatus
import org.example.infra.http.controllers.ContextHelpers.contextUser
import org.example.infra.http.controllers.ContextHelpers.handleError
import org.example.infra.http.controllers.ContextHelpers.validId

object PostController {

    fun create(ctx: Context) {
        val req = ctx.bodyAsClass(PostCreateReqDto::class.java)
        val owner = ctx.contextUser() ?: return

        val createPostUseCase = CreatePostUseCase(SQLitePostRepository(), SQLiteUserRepository())
        when( val res = createPostUseCase.execute(req, owner)) {
            is UseCaseResult.Success -> {
                ctx.status(201).json(res.data)
            }
            is UseCaseResult.NotAllowedError -> {
                ctx.handleError(HttpStatus.FORBIDDEN, res.message)
            }
            else -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        }
    }

    fun getMyPosts(ctx: Context) {
        val contextUser = ctx.contextUser() ?: return

        val getPostByUserUseCase = GetPostsByUserUseCase(SQLitePostRepository())
        val res = getPostByUserUseCase.execute(contextUser)
        ctx.json(res)
    }

    fun getUserPosts(ctx: Context) {
//        val owner = ctx.contextUser() ?: return
        val id = ctx.validId() ?: return

        val getPostByUserUseCase = GetPostsByUserUseCase(SQLitePostRepository())
        val res = getPostByUserUseCase.execute(id)
        ctx.json(res)
    }
}