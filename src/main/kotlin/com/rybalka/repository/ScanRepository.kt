package com.rybalka.repository

import com.rybalka.model.ScanData

interface ScanRepository {
    fun save(id: String, result: ScanData)
    fun load(id: String): ScanData
}