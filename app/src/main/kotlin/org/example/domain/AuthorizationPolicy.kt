package org.example.domain

import org.example.domain.posts.Post
import org.example.domain.users.User
import org.example.domain.users.UserType

enum class Action {
    CREATE,     // can create a new resource
    VIEW,       // can view a resource
    EDIT,       // can edit a resource
    EXCLUDE     // can exclude a resource
}

interface AuthorizationPolicy<T> {
    fun isAuthorized(user: User, action: Action, resource: T): Boolean
}

/**
 * Independente do framework que eu esteja utizando, as ações que um usuário pode fazer em um post são
 * nem estabelecidas, pois são regras de negócio
 * criar post: moderador não podem criar posts, apenas moderá-los
 * ver post: basta o post que eu estou tentando ver não seja privado ou eu seja o dono dele
 * editar post: só o dono do post pode alterar os dados dele
 * excluir post: apenas moderadores e o dono do post podem excluí-lo
 */
class PostAuthorizationPolicy: AuthorizationPolicy<Post> {
    override fun isAuthorized(user: User, action: Action, resource: Post): Boolean {
        return when(action) {
            Action.CREATE -> user.type != UserType.MODERATOR
            Action.VIEW -> !resource.isPrivate || user.id == resource.owner
            Action.EDIT -> user.id == resource.owner
            Action.EXCLUDE -> user.type == UserType.MODERATOR || user.id == resource.owner
        }
    }
}