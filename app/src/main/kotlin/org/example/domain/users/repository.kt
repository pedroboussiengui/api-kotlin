package org.example.domain.users

interface UserRepository {
    fun addUser(user: User): Result<Long>
    fun getAll(): List<User>
    fun getByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun getById(id: Long): Result<User>
    fun update(id: Long, newUser: User): Result<Long>
    fun remove(id: Long): Result<Boolean>
    fun setAddress(id: Long, address: Address): Result<User>
}

//class InMemoryRepository : UserRepository {
//    private val db = mutableMapOf<Long, User>()
//
//    override fun addUser(user: User) {
//        db.put(user.id, user);
//    }
//
//    override fun getAll(): List<User> {
//        return db.values.toList()
//    }
//
//    override fun getByEmail(email: String): User? {
//        return db.values.find { it.email == email }
//    }
//
//    override fun getById(id: Long): Result<User> {
//        return db.values.find { it.id == id }
//            ?.let { return Result.success(it) }
//            ?: return Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
//    }
//
//    override fun update(id: Long, newData: UserUpdateReqDto): Result<User> {
//        return db.values.find { it.id == id }
//            ?.apply {
//                newData.username?.let { this.username = it }
//                newData.password?.let { this.password = it }
//                newData.email?.let { this.email = it }
//            }?.let {
//                Result.success(it)
//            } ?: Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
//    }
//
//    override fun remove(id: Long): Result<Boolean> {
//        return db.values.find { it.id == id }
//            ?.let {
//                db.remove(id)
//                return Result.success(true)
//            }
//            ?: return Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
//    }
//
//    override fun setAddress(id: Long, address: Address): Result<User> {
//        return db.values.find { it.id == id }
//            ?.apply {
//                this.address = address
//            }?.let {
//                Result.success(it)
//            } ?: Result.failure(ApiError.NotFoundError("User with ID $id was not found"))
//    }
//}