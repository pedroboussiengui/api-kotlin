package org.example.adapter

import java.io.InputStream

data class PostCreateReqDto(
        val title: String,
        val content: String
)

data class ModUserCreateReqDto(
        val username: String,
        val password: String,
        val email: String
)

data class ModUserCreateResDto(
        var id: Long,
        var username: String,
        var email: String
)

data class FileReqDto(
        val content: InputStream,
        val size: Long,
        val extension: String
)

data class UserCreateReqDto(
        val username: String,
        val password: String,
        val email: String
)

data class UserOutput(
        var id: Long,
        var username: String,
        var avatarUrl: String?,
        var email: String
)

data class UserWithAddressOutput(
        var id: Long,
        var username: String,
        var email: String,
        var address: UserAddressReqDto?
)

class UserAddressReqDto(
        var cep: String,
        var rua: String,
        var numero: Int,
        var bairro: String,
        var cidade: String,
        var estado: String
)

data class UserUpdateReqDto(
        val username: String?,
        val email: String?
)

data class AuthenticationPassowordReqDto(
        val email: String,
        val password: String
)

data class AuthenticationPassowordResDto(
        val cookieSecret: String,
        val message: String
)

data class FishshSessionOutput(
        val message: String
)