package org.example.infra.http.controllers

import io.javalin.http.Context
import io.javalin.http.Cookie
import org.example.application.UseCaseResult
import org.example.application.usecases.auth.AuthenticationPassowordReqDto
import org.example.application.usecases.auth.AuthenticationPassowordResDto
import org.example.application.usecases.auth.PasswordAuthenticationUseCase
import org.example.infra.bcrypt.BCryptPasswordHasher
import org.example.infra.database.ktorm.repositories.SQLiteUserRepository
import org.example.infra.http.HttpStatus
import org.example.infra.http.controllers.ContextHelpers.handleError
import org.example.infra.redis.RedisInMemoryUserDAO

object AuthenticationController {
    fun authenticateByPassword(ctx: Context) {
        val req = ctx.bodyAsClass(AuthenticationPassowordReqDto::class.java)

        val passwordAuthenticationUseCase = PasswordAuthenticationUseCase(
                SQLiteUserRepository(), BCryptPasswordHasher(), RedisInMemoryUserDAO()
        )
        when (val res = passwordAuthenticationUseCase.execute(req)) {
            is UseCaseResult.Success -> {
                val sessionCookie = Cookie("session_id", res.data.toString()).apply {
                    isHttpOnly = true
                    maxAge = 60 * 60
                    path = "/"
                }
                ctx.cookie(sessionCookie)
                ctx.json("User successfully authenticated")
            }
            is UseCaseResult.NotFoundError, is UseCaseResult.BusinessRuleError -> {
                ctx.handleError(HttpStatus.UNAUTHORIZED, "email or password are incorrect")
            }
            else -> {
                ctx.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        }
    }
}