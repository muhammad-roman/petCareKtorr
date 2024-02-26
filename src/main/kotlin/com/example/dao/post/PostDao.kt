package com.example.dao.post

import com.example.model.Post

interface PostDao {
    suspend fun allPosts(): List<Post>
    suspend fun postName(name: String): List<Post>
    suspend fun postId(postId: Int): Post?
    suspend fun addNewPost(tittle: String, owner: Int, reciver: Int, offers: String, postPhoto: String, description: String, serviceType: String, serviceTime: String, postDate: String, reward: String, location: String):Post?
    suspend fun editPost(postId: Int, tittle: String, postPhoto: String, description: String,serviceType: String, serviceTime: String, postDate: String, reward: String, location: String): Boolean
    suspend fun deletePost (postId: Int): Boolean
}

