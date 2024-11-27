package org.example.infra.http.controllers

import io.javalin.http.Context
import org.example.adapter.PostCreateReqDto
import org.example.application.Container
import org.example.application.usecases.post.CreatePostUseCase
import org.example.application.usecases.post.GetMyPostsUseCase
import org.example.infra.database.ktorm.repositories.SQLitePostRepository
import org.example.infra.database.ktorm.repositories.SQLiteUserRepository
import org.example.infra.http.HttpStatus
import org.example.infra.http.controllers.ContextHelpers.contextUser
import org.example.infra.http.controllers.ContextHelpers.handleError
import org.example.infra.http.controllers.ContextHelpers.handleException
import org.example.infra.redis.RedisInMemoryUserDAO

object PostController {

    fun create(ctx: Context) {
        val req = ctx.bodyAsClass(PostCreateReqDto::class.java)
        val owner = ctx.contextUser() ?: return

        val createPostUseCase = CreatePostUseCase(SQLitePostRepository(), SQLiteUserRepository())
        when( val res = createPostUseCase.execute(req, owner)) {
            is Container.Success -> {
                ctx.status(201).json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }

    fun getMyPosts(ctx: Context) {
        val sessionCookie = ctx.cookie("session_id")
        if (sessionCookie == null) {
            ctx.handleError(HttpStatus.UNAUTHORIZED, "Authentication failed")
            return
        }

        val getPostByUserUseCase = GetMyPostsUseCase(SQLitePostRepository(), RedisInMemoryUserDAO())

        when(val res = getPostByUserUseCase.execute(sessionCookie)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                handleException(ctx, res.value)
            }
        }
    }
}