//package org.example.application.usecases
//
//import org.example.ApiError
//import org.example.HttpStatus
//import org.example.UserUpdateReqDto
//import org.example.domain.users.User
//import org.example.domain.users.UserRepository
//import org.example.infra.repository.SQLiteUserRepository
//
//class UserUpdateReqDto(
//    val username: String?,
//    val email: String?
//)
//
//class UpdateUserUseCase() {
//
//    private val userDb: UserRepository = SQLiteUserRepository()
//
//    fun execute(id: Long, input: UserUpdateReqDto): Result<Long> {
//
//        val user: User = userDb.getById(id).getOrElse { err ->
//            if (err is ApiError.NotFoundError) {
//                return Result.failure(ApiError.NotFoundError(HttpStatus.NOT_FOUND.message))
//            }
//            throw err
//        }
//
//        // atribuo os campos e não forem null
//        input.username?.let { user.username = it }
//        input.email?.let { user.email = it }
//
//        // email não pode existir
//        if (userDb.existsByEmail(user.email)) {
//            ctx.handleError(HttpStatus.CONFLICT, "Business rule error: E-mail already exists")
//            return@patch
//        }
//
//        // valido o user com os dados novos
//        user.isValid().onFailure { err ->
//            if (err is ApiError.ValidationError) {
//                ctx.handleError(HttpStatus.BAD_REQUEST, "Validation error", err.errors)
//                return@patch
//            }
//        }
//        // caso sucesso, ele atualiza com os dados existentes
//        val updatedUserId: Long = userDb.update(id, user).getOrElse { err ->
//            if (err is ApiError.NotFoundError) ctx.handleError(HttpStatus.NOT_FOUND, err.message)
//            return@patch
//        }
//
//
//        return updatedUserId
//    }
//
//
//
//}