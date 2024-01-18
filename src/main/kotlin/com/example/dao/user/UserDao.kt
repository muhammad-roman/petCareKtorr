package com.example.dao.user

import com.example.model.SignUpParams
import com.example.model.User

interface UserDao {
    suspend fun insert(params: SignUpParams): User?
    suspend fun findByEmail(email: String): User?
}