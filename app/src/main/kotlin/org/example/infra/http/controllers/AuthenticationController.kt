package org.example.infra.http.controllers

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context
import io.javalin.http.Cookie
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.example.adapter.AuthenticationPassowordReqDto
import org.example.application.Container
import org.example.application.usecases.auth.FinishSessionUseCase
import org.example.application.usecases.auth.PasswordAuthenticationUseCase
import org.example.infra.bcrypt.BCryptPasswordHasher
import org.example.infra.database.ktorm.repositories.SQLiteUserRepository
import org.example.infra.environments.Environment
import org.example.infra.http.HttpStatus
import org.example.infra.http.controllers.ContextHelpers.handleError
import org.example.infra.redis.RedisInMemoryUserDAO

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubAccessTokenResponse(val access_token: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubUserResponse(val id: Long, val login: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubUserEmailResponse(
        val email: String,
        val primary: Boolean,
        val verified: Boolean,
        val visibility: String?
)

object AuthenticationController {
    private val env = Environment()
    private val client = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()

    fun authenticateByPassword(ctx: Context) {
        val req = ctx.bodyAsClass(AuthenticationPassowordReqDto::class.java)

        val passwordAuthenticationUseCase = PasswordAuthenticationUseCase(
                SQLiteUserRepository(), BCryptPasswordHasher(), RedisInMemoryUserDAO()
        )
        when (val res = passwordAuthenticationUseCase.execute(req)) {
            is Container.Success -> {
                val sessionCookie = Cookie("session_id", res.value.cookieSecret).apply {
                    isHttpOnly = true
                    maxAge = 60 * 60
                    path = "/"
                }
                ctx.cookie(sessionCookie)
                ctx.json(res.value)
            }
            is Container.Failure -> {
                ContextHelpers.handleException(ctx, res.value)
            }
        }
    }

    fun finishSession(ctx: Context) {
        val sessionCookie = ctx.cookie("session_id")
        if (sessionCookie == null) {
            ctx.handleError(HttpStatus.UNAUTHORIZED, "Authentication failed")
            return
        }

        val finishSessionUseCase = FinishSessionUseCase(RedisInMemoryUserDAO())
        when (val res = finishSessionUseCase.execute(sessionCookie)) {
            is Container.Success -> {
                ctx.json(res.value)
            }
            is Container.Failure -> {
                ContextHelpers.handleException(ctx, res.value)
            }
        }
    }

    fun authenticateByGithub(ctx: Context) {
        val clientId = env.get("github.oauth.clientId")
        val redirectUri = "http://localhost:7070/callback/github"
        val scope = "read:user%20user:email"
        ctx.redirect("https://github.com/login/oauth/authorize?client_id=$clientId&redirectUri=$redirectUri&scope=$scope")
    }

    fun githubCallback(ctx: Context) {
        val code = ctx.queryParam("code")
        if (code == null) {
            ctx.status(400).result("Code not found")
            return
        }
        try {
            val accessToken = requestAccessToken(code)
            val user = fetchGithubUser(accessToken)
            val email = fetchGithubUserEmail(accessToken)
            ctx.json(mapOf("id" to user.id, "login" to user.login, "email" to email))
        } catch (e: Exception) {
            ctx.status(500).result("Erro ao processar: ${e.message}")
        }
    }

    private fun requestAccessToken(code: String): String {
        val formBody = FormBody.Builder()
                .add("client_id", env.get("github.oauth.clientId"))
                .add("client_secret", env.get("github.oauth.clientSecret"))
                .add("code", code)
                .build()

        val request = Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(formBody)
                .header("Accept", "application/json")
                .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Erro ao gerar o token: ${response.message}")

        val responseBody = response.body?.string() ?: throw Exception("Corpo da resposta vazio")
        return objectMapper.readValue<GithubAccessTokenResponse>(responseBody).access_token
    }

    private fun fetchGithubUser(accessToken: String): GithubUserResponse {
        val request = Request.Builder()
                .url("https://api.github.com/user")
                .get()
                .header("Authorization", "Bearer $accessToken")
                .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Erro ao obter dados do usuário: ${response.message}")

        val responseBody = response.body?.string() ?: throw Exception("Corpo da resposta vazio")
        val user: GithubUserResponse = objectMapper.readValue(responseBody)
        return user
    }

    private fun fetchGithubUserEmail(accessToken: String): String {
        val request = Request.Builder()
                .url("https://api.github.com/user/emails")
                .get()
                .header("Authorization", "Bearer $accessToken")
                .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Erro ao obter dados do usuário: ${response.message}")

        val responseBody = response.body?.string() ?: throw Exception("Corpo da resposta vazio")
        val emails: List<GithubUserEmailResponse> = objectMapper.readValue(responseBody)

        val primaryEmail = emails.firstOrNull { it.primary }
                ?: throw Exception("Nenhum e-mail primário encontrado")
        return primaryEmail.email
    }
}