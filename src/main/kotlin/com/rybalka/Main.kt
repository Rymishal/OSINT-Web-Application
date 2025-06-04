package com.rybalka

import com.rybalka.repository.ScanDataRepository
import com.rybalka.repository.ScanElementRepository
import com.rybalka.service.ScanMode
import com.rybalka.service.ScanService
import com.rybalka.tools.Adapter
import com.rybalka.tools.AmassStrategy
import com.rybalka.tools.HarvesterStrategy

suspend fun main(args: Array<String>) {
    val mode = ScanMode.parse(args)
    val service = ScanService(
        ScanDataRepository(),
        ScanElementRepository(),
        Adapter(HarvesterStrategy()),
        Adapter(AmassStrategy())
    )

    when (mode) {
        is ScanMode.Scan -> service.runScan(mode.domain, mode.outputType, mode.filename)
        is ScanMode.Retrieve -> service.retrieveScan(mode.id, mode.outputType, mode.filename)
    }
}