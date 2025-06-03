package com.rybalka.export

import com.rybalka.model.ScanData

class StdoutExporter : ResultExporter {
    override fun export(scanData: ScanData) {
        println("Scan ${scanData.id} ${scanData.domain} Results:")
        scanData.elements!!.forEach { item ->
            println("${item.id} ${item.scanId} ${item.value}")
        }
    }
}