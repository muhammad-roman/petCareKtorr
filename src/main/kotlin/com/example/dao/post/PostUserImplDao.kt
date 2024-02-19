package com.example.dao.post

import com.example.dao.DatabaseFactory
import com.example.model.PostRow
import com.example.model.PostUser
import com.example.model.PostUsersRow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class PostUserImplDao: PostUserDao {

    private fun resultRowToPostUsuario(row: ResultRow) = PostUser(
        postUserId = row[PostUsersRow.postUserId],
        postUserDate = row[PostUsersRow.postUserDate],
        postId =  row[PostUsersRow.postId],
        id =  row[PostUsersRow.id]
    )

    override suspend fun postsEncontrados(user_id: Int): List<PostUser> = DatabaseFactory.dbQuery {
        PostUsersRow
            .select { PostUsersRow.id eq user_id }
            .map(::resultRowToPostUsuario)
    }


    override suspend fun addPostEncontrado(postIdUser: Int, fecha: String, idPost: Int, usuarioId: Int): PostUser? =
        DatabaseFactory.dbQuery {
            val insertStatement = PostUsersRow.insert {
                it[postUserId] = postIdUser
                it[postUserDate] = fecha
                it[postId] = idPost
                it[id] = usuarioId
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToPostUsuario)
        }

    override suspend fun deletePost(id: Int, usu_id: Int): Boolean = DatabaseFactory.dbQuery {
        PostRow.deleteWhere { PostUsersRow.postId eq id and PostUsersRow.id.eq(usu_id) }
    }> 0


}
val daoPostUsuario: PostUserDao = PostUserImplDao().apply {}