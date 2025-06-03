package com.rybalka.repository

import com.rybalka.DatabaseConfig
import com.rybalka.model.ScanData
import java.util.*

class ScanDataRepository {

    fun save(scanData: ScanData): ScanData {
        val sql = "INSERT INTO scan_data (domain) VALUES (?) RETURNING id, domain"
        DatabaseConfig.getConnection().prepareStatement(sql).use { statement ->
            statement.setString(1, scanData.domain)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) (
                ScanData(
                    resultSet.getString("id"),
                    resultSet.getString("domain"),
                    emptyList()
                )
            ) else {
                throw RuntimeException("Failed to insert scan_data")
            }
        }
    }

    fun findById(id: String): ScanData? {
        val sql = "SELECT * FROM scan_data WHERE id = ?"
        DatabaseConfig.getConnection().prepareStatement(sql).use { statement ->
            statement.setObject(1, UUID.fromString(id))
            val rs = statement.executeQuery()
            return if (rs.next()) {
                ScanData(rs.getString("id"),
                    rs.getString("domain"),
                    emptyList())
            } else null
        }
    }
}