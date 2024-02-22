package com.example.dao.post

import com.example.dao.DatabaseFactory.dbQuery
import com.example.model.Post
import com.example.model.PostRow
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PostDaoImpl: PostDao {

    private fun resultRowToPost(row: ResultRow) = Post(
        postId = row[PostRow.postId],
        owner = row[PostRow.owner],
        reciver = row[PostRow.reciver],
        offers = row[PostRow.offers],
        tittle = row[PostRow.tittle],
        postPhoto = row[PostRow.postPhoto],
        description = row[PostRow.description],
        serviceType = row[PostRow.serviceType],
        serviceTime = row[PostRow.serviceTime],
        postDate = row[PostRow.postDate],
        reward = row[PostRow.reward],
        location = row[PostRow.location]
    )
    override suspend fun allPosts(): List<Post> = dbQuery{
        PostRow.selectAll().map(::resultRowToPost)
    }


    override suspend fun postName(name: String): List<Post> = dbQuery {
        PostRow
            .select {PostRow.tittle eq name}
            .map(::resultRowToPost)
    }
    override suspend fun postId(postId: Int): Post?= dbQuery {
        PostRow
            .select {PostRow.postId eq postId}
            .map(::resultRowToPost)
            .singleOrNull()
    }

    override suspend fun addNewPost(
        owner: Int,
        reciver: Int,
        offers: String,
        tittle: String,
        postPhoto: String,
        description: String,
        serviceType: String,
        serviceTime: String,
        postDate: String,
        reward: String,
        location: String
    ): Post? = dbQuery {
        val insertStatement = PostRow.insert {
            it[PostRow.owner] = owner
            it[PostRow.reciver] = reciver
            it[PostRow.offers] = offers
            it[PostRow.tittle] = tittle
            it[PostRow.postPhoto] = postPhoto
            it[PostRow.description] = description
            it[PostRow.serviceType] = serviceType
            it[PostRow.serviceTime] = serviceTime
            it[PostRow.postDate] = postDate
            it[PostRow.reward] = reward
            it[PostRow.location] = location
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToPost)
    }

    override suspend fun editPost(
        postId: Int,
        tittle: String,
        postPhoto: String,
        description: String,
        serviceType: String,
        serviceTime: String,
        postDate: String,
        reward: String,
        location: String
    ): Boolean = dbQuery {
        PostRow.update({PostRow.postId eq postId}) {
            it[PostRow.tittle] = tittle
            it[PostRow.postPhoto] = postPhoto
            it[PostRow.description] = description
            it[PostRow.serviceType] = serviceType
            it[PostRow.serviceTime] = serviceTime
            it[PostRow.postDate] = postDate
            it[PostRow.reward] = reward
            it[PostRow.location] = location
        }
    } > 0



    override suspend fun deletePost(postId: Int): Boolean = dbQuery {
        PostRow.deleteWhere { PostRow.postId eq postId } > 0
    }

}

val dao: PostDao = PostDaoImpl().apply {
    runBlocking {
        if(allPosts().isEmpty()) {
            addNewPost(1,2,"14","Post de relleno", "image.png", "No descripcion...", "Sin servicio", "0h", "00/00/0000","1000","Barcelona" )
        }
    }
}



