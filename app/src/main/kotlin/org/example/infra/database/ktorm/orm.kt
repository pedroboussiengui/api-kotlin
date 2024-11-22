package org.example.infra.database.ktorm

import org.ktorm.entity.Entity
import org.ktorm.schema.*

interface UserDb : Entity<UserDb> {
    companion object : Entity.Factory<UserDb>()

    var id: Long
    var username: String
    var password: String
    var email: String
    var type: String
    var cep: String?
    var rua: String?
    var numero: Int?
    var bairro: String?
    var cidade: String?
    var estado: String?
}

object Users : Table<UserDb>("users_tb") {
    val id = long("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val password = varchar("password").bindTo { it.password }
    val email = varchar("email").bindTo { it.email }
    var type = varchar("type").bindTo { it.type }
    val cep = varchar("address_cep").bindTo { it.cep }
    val rua = varchar("address_rua").bindTo { it.rua }
    val numero = int("address_numero").bindTo { it.numero }
    val bairro = varchar("address_bairro").bindTo { it.bairro }
    val cidade = varchar("address_cidade").bindTo { it.cidade }
    val estado = varchar("address_estado").bindTo { it.estado }
}

interface PostDb : Entity<PostDb> {
    companion object : Entity.Factory<PostDb>()

    var id: Long
    var title: String
    var content: String
    var timestamp: String
    var likes: Int
    var isPrivate: Boolean
    var owner: UserDb
}

object Posts : Table<PostDb>("posts_tb") {
    val id = long("id").primaryKey().bindTo { it.id }
    val title = varchar("title").bindTo { it.title }
    val content = text("content").bindTo { it.content }
    val timestamp = varchar("timestamp").bindTo { it.timestamp }
    val likes = int("likes").bindTo { it.likes }
    val isPrivate = boolean("is_private").bindTo { it.isPrivate }
    val userId = long("user_id").bindTo { it.owner.id }
}