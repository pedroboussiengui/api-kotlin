package org.example.infra.database.sqlite

import org.ktorm.database.Database

object DatabaseSingleton {
    val database: Database by lazy {
        Database.connect("jdbc:sqlite:sample.db")
    }
}