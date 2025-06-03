package com.rybalka

import com.rybalka.repository.ScanDataRepository
import com.rybalka.repository.ScanElementRepository
import com.rybalka.service.ScanMode
import com.rybalka.service.ScanService
import com.rybalka.tools.AmassAdapter
import com.rybalka.tools.HarvesterAdapter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

private val logger: Logger? = LogManager.getLogger("ScanService")

suspend fun main(args: Array<String>) {
    val mode = ScanMode.parse(args)
    val service = ScanService(
        ScanDataRepository(),
        ScanElementRepository(),
        HarvesterAdapter(),
        AmassAdapter()
    )

    when (mode) {
        is ScanMode.Scan -> service.runScan(mode.domain, mode.outputType, mode.filename)
        is ScanMode.Retrieve -> service.retrieveScan(mode.id, mode.outputType, mode.filename)
    }
}