package com.rybalka.service

import com.rybalka.export.ResultExporterFactory
import com.rybalka.filter.ResultFilter
import com.rybalka.model.ScanData
import com.rybalka.repository.ScanDataRepository
import com.rybalka.repository.ScanElementRepository
import com.rybalka.tools.AmassAdapter
import com.rybalka.tools.HarvesterAdapter
import com.rybalka.util.ToolFailureException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ScanService(private val scanDataRepository: ScanDataRepository,
                  private val scanElementRepository: ScanElementRepository,
                  private val harvesterAdapter: HarvesterAdapter,
                  private val amassAdapter: AmassAdapter) {
    private val logger: Logger? = LogManager.getLogger("ScanService")

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
                    logger?.info("Harvester started for scan: ${scanData.id}  ${scanData.domain}")
                    val raw = harvesterAdapter.run(domain)
                    logger?.info("Harvester successfully finished")
                    ResultFilter(Regex("^(?!.*\\*)(?=.*( : |@))(?=.*\\.).+")).filter(raw, scanData.id!!)
                } catch (e: Exception) {
                    logger?.warn(e.message!!)
                    harvesterException = ToolFailureException(e.message!!)
                    emptySet()
                }
            },
            async {
                try {
                    logger?.info("Amass started for scan: ${scanData.id}  ${scanData.domain}")
                    val raw = amassAdapter.run(domain)
                    logger?.info("Amass successfully finished")
                    ResultFilter(Regex(" --> ")).filter(raw, scanData.id!!)

                } catch (e: Exception) {
                    logger?.warn(e.message!!)
                    amassException = ToolFailureException(e.message!!)
                    emptySet()
                }
            }
        ).flatten().toSet()

        if(harvesterException != null && amassException != null) {
            throw ToolFailureException("${harvesterException!!.message} ${amassException!!.message}")
        }

        scanData.elements = scanElementRepository.saveAll(results)
        val exporter = ResultExporterFactory.createExporter(outputType, fileName)
        exporter.export(scanData)
    }

    fun retrieveScan(id: String, outputType: OutputType, fileName: String) {
        logger?.info("Retrieving scan: $id")
        val scanData = scanDataRepository.findById(id)
        val scanElementRepository = scanElementRepository
        scanData?.elements = scanElementRepository.findAllByScanId(id)
        val exporter = ResultExporterFactory.createExporter(outputType, fileName)
        if (scanData != null) {
            exporter.export(scanData)
        } else {
            exporter.export(
                ScanData(
                    id, "", null
                )
            )
        }
    }
}