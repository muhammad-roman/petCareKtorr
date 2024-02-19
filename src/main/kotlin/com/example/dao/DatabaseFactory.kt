package com.example.dao

import com.example.model.PostRow
import com.example.model.UserRow
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {
    fun init(){
        Database.connect(createHikariDatasource())
        transaction {
            SchemaUtils.create(UserRow)
            SchemaUtils.create(PostRow)
        }
    }
// ta conectado a elephant sql lo que etsa en comentario son los datos de la base de datos en local para pruebas
    private fun createHikariDatasource(): HikariDataSource{
    val driverClass = "org.postgresql.Driver"
    //val jdbcUrl = "jdbc:postgresql://localhost:5432/petCare"
        val jdbcUrl = "jdbc:postgresql://flora.db.elephantsql.com:5432/egkgayxz"

    val hikariConfig = HikariConfig().apply {
        driverClassName = driverClass
        setJdbcUrl(jdbcUrl)
        //username = "postgres"
        username = "egkgayxz"
        //password = "6ftxkyetyxpr"
        password = "DaNhYB_qDELGkXu1x9Dnm87qYIrYWAxH"
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()

        }
        return HikariDataSource(hikariConfig)
    }
    suspend fun <T> dbQuery(block: suspend () -> T) =
        newSuspendedTransaction(Dispatchers.IO) { block()}
}