package org.example

object Database {
    private val db = mutableMapOf<Long, User>()

    fun addUser(user: User) {
        db.put(user.id, user);
    }

    fun getAll(): List<User> {
        return db.values.toList()
    }

    fun getByEmail(email: String): User? {
        return db.values.find { it.email == email }
    }

    fun getById(id: Long): Result<User> {
        return db.values.find { it.id == id }
            ?.let { return Result.success(it) }
            ?: return Result.failure(NotFoundError("User with ID $id was not found"))
    }

    fun update(id: Long, newData: UserUpdateReqDto): Result<User> {
        return db.values.find { it.id == id }
            ?.apply {
                newData.username?.let { this.username = it }
                newData.password?.let { this.password = it }
                newData.email?.let { this.email = it }
            }?.let {
                Result.success(it)
            } ?: Result.failure(NotFoundError("User with ID $id was not found"))
    }

    fun remove(id: Long): Result<Boolean> {
        return db.values.find { it.id == id }
            ?.let {
                db.remove(id)
                return Result.success(true)
            }
            ?: return Result.failure(NotFoundError("User with ID $id was not found"))
    }
}