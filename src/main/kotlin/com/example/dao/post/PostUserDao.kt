package com.example.dao.post

import com.example.model.PostUser
import java.time.LocalDate

interface PostUserDao {
    suspend fun postsEncontrados(user_id: Int): List<PostUser>
    suspend fun addPostEncontrado(postUserId: Int, fecha: String, postId: Int, usuarioId: Int): PostUser?
    suspend fun deletePost (id: Int, usu_id: Int): Boolean
}