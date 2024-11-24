package org.example.infra.database.sqlite

import org.example.infra.environments.Environment
import org.ktorm.database.Database

object DatabaseSingleton {
    private val env: Environment = Environment()
    private val url = env.get("database.url")

    val database: Database by lazy {
        Database.connect(url)
    }
}