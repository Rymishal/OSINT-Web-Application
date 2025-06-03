package com.rybalka.repository

import com.rybalka.DatabaseConfig
import com.rybalka.model.ScanElement
import java.util.*

class ScanElementRepository {

    fun saveAll(scanElements: Set<ScanElement>): List<ScanElement> {
        if (scanElements.isEmpty()) return emptyList()
        val placeholders = scanElements.joinToString(", ") { "(?, ?)" }
        val sql = "INSERT INTO scan_element (scan_id, value) VALUES $placeholders RETURNING id, scan_id, value"

        val insertedElements = mutableListOf<ScanElement>()
        DatabaseConfig.getConnection().prepareStatement(sql).use { statement ->
            var index = 1
            for (element in scanElements) {
                statement.setObject(index++, UUID.fromString(element.scanId))
                statement.setString(index++, element.value)
            }
            val res = statement.executeQuery()
            while (res.next()) {
                insertedElements.add(
                    ScanElement(
                        id = res.getString("id"),
                        scanId = res.getString("scan_id"),
                        value = res.getString("value")
                    )
                )
            }
        }
        return insertedElements
    }

    fun findAllByScanId(scanId: String): List<ScanElement> {
        val sql = "SELECT * FROM scan_element WHERE scan_id = ?"
        DatabaseConfig.getConnection().prepareStatement(sql).use { stmt ->
            stmt.setObject(1, UUID.fromString(scanId))
            val rs = stmt.executeQuery()
            val results = mutableListOf<ScanElement>()
            while (rs.next()) {
                results.add(
                    ScanElement(
                        rs.getString("id"),
                        rs.getString("scan_id"),
                        rs.getString("value")
                    )
                )
            }
            return results
        }
    }
}