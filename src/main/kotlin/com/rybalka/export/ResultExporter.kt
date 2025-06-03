package com.rybalka.export

import com.rybalka.model.ScanData

interface ResultExporter {
    fun export(scanData: ScanData)
}