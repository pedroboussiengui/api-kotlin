package org.example.infra.database.sqlite

import org.ktorm.database.Database

fun initDatabase(): Database {
    return Database.connect("jdbc:sqlite:sample.db")
}