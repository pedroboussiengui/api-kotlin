package org.example.infra.database.sqlite

import org.ktorm.database.Database

object DatabaseSingleton {
    private val url = "jdbc:sqlite:sample.db"

    val database: Database by lazy {
        Database.connect(url)
    }
}