package com.rybalka.service

import com.rybalka.export.ResultExporterFactory
import com.rybalka.filter.ResultFilter
import com.rybalka.model.ScanData
import com.rybalka.repository.ScanDataRepository
import com.rybalka.repository.ScanElementRepository
import com.rybalka.tools.Adapter
import com.rybalka.util.IncorrectIdException
import com.rybalka.util.ToolFailureException
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ScanService(private val scanDataRepository: ScanDataRepository,
                  private val scanElementRepository: ScanElementRepository,
                  private val harvesterAdapter: Adapter,
                  private val amassAdapter: Adapter) {
    private val logger = KotlinLogging.logger {}

    suspend fun runScan(domain: String, outputType: OutputType, fileName: String) = coroutineScope {
        val scanData = scanDataRepository.save(
            ScanData(
                null, domain, emptyList()
            )
        )
        var harvesterException: ToolFailureException? = null
        var amassException: ToolFailureException? = null
        val results = awaitAll(
            async {
                try {
                    logger.info { "Harvester started for scan: ${scanData.id}  ${scanData.domain}"}
                    val raw = harvesterAdapter.run(domain)
                    logger.info { "Harvester successfully finished" }
                    ResultFilter(Regex("^(?!.*\\*)(?=.*( : |@))(?=.*\\.).+")).filter(raw, scanData.id!!)
                } catch (e: Exception) {
                    logger.warn { e.message!! }
                    harvesterException = ToolFailureException(e.message!!)
                    emptySet()
                }
            },
            async {
                try {
                    logger.info { "Amass started for scan: ${scanData.id}  ${scanData.domain}" }
                    val raw = amassAdapter.run(domain)
                    logger.info { "Amass successfully finished" }
                    ResultFilter(Regex(" --> ")).filter(raw, scanData.id!!)

                } catch (e: Exception) {
                    logger.warn { e.message!! }
                    amassException = ToolFailureException(e.message!!)
                    emptySet()
                }
            }
        ).flatten().toSet()

        if(harvesterException != null && amassException != null) {
            val message = """Both tools failed with such exceptions:
                    TheHarvester:
                    ${harvesterException!!.message} 
                    Amass:
                    ${amassException!!.message}"""
            logger.error { message }
            throw ToolFailureException(message)
        }

        scanData.elements = scanElementRepository.saveAll(results)
        val exporter = ResultExporterFactory.createExporter(outputType, fileName)
        exporter.export(scanData)
        return@coroutineScope scanData
    }

    fun retrieveScan(id: String, outputType: OutputType, fileName: String): ScanData? {
        logger.info { "Retrieving scan: $id" }
        val scanData = scanDataRepository.findById(id)
        val scanElementRepository = scanElementRepository
        scanData?.elements = scanElementRepository.findAllByScanId(id)
        val exporter = ResultExporterFactory.createExporter(outputType, fileName)
        if (scanData != null) {
            exporter.export(scanData)
        } else {
            val message = "No scan found for id: $id"
            logger.error { message }
            throw IncorrectIdException(message)
        }
        return scanData
    }
}