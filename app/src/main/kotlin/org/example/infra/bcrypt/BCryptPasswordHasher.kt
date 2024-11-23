package org.example.infra.bcrypt

import org.example.adapter.PasswordHasher
import org.mindrot.jbcrypt.BCrypt

class BCryptPasswordHasher: PasswordHasher {
    override fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    override fun verify(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}