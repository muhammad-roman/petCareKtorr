package com.example.dao.user

import com.example.dao.DatabaseFactory.dbQuery
import com.example.model.SignUpParams
import com.example.model.User
import com.example.model.UserRow
import com.example.security.hashpassword
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserDaoImpl : UserDao {
    override suspend fun insert(params: SignUpParams): User? {
        return dbQuery{
            val insertStatement = UserRow.insert {
                it[name] = params.name
                it[email] = params.email
                it[password] = hashpassword(params.password)
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                rowToUser(it)
            }
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return dbQuery {
            UserRow.select{ UserRow.email eq email}
                .map {rowToUser(it) }
                .singleOrNull()
        }
    }

    private fun rowToUser(row: ResultRow): User{
        return User(
            id = row[UserRow.id],
            name = row[UserRow.name],
            bio = row[UserRow.bio],
            avatar = row[UserRow.avatar],
            password = row[UserRow.password]
        )
    }
}