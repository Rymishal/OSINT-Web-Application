package com.rybalka

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object DatabaseConfig {
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:postgresql://localhost:2432/osintdb"
        username = System.getenv("DB_USER") ?: "osintuser"
        password = System.getenv("DB_PASSWORD") ?: "osintpass"
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 5
        isAutoCommit = true
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }

    private val dataSource = HikariDataSource(hikariConfig)

    fun getConnection(): Connection = dataSource.connection
}