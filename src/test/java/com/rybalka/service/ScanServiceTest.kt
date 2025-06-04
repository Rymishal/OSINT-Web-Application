package com.rybalka.service

import com.rybalka.model.ScanData
import com.rybalka.model.ScanElement
import com.rybalka.repository.ScanDataRepository
import com.rybalka.repository.ScanElementRepository
import com.rybalka.tools.AmassAdapter
import com.rybalka.tools.HarvesterAdapter
import com.rybalka.util.IncorrectIdException
import com.rybalka.util.ToolFailureException
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ScanServiceTest {

    private val scanDataRepository = mockk<ScanDataRepository>()
    private val scanElementRepository = mockk<ScanElementRepository>()
    private val harvesterAdapter = mockk<HarvesterAdapter>()
    private val amassAdapter = mockk<AmassAdapter>()

    private lateinit var scanService: ScanService


    @BeforeEach
    fun setUp() {
        scanService = ScanService(
            scanDataRepository,
            scanElementRepository,
            harvesterAdapter,
            amassAdapter
        )
    }

    @Test
    fun successfulScan() = runBlocking {
        val domain = "example.com"
        val scanData = ScanData(
            id = UUID.randomUUID().toString(), domain = domain,
            elements = emptyList()
        )

        val harvesterResults = listOf("user@example.com", "login.example.com")
        val amassResults = listOf("mail.example.com", "login.example.com")

        every { scanDataRepository.save(any()) } returns scanData
        every { scanElementRepository.saveAll(any()) } returns
                listOf(
                    ScanElement(id = UUID.randomUUID().toString(), scanData.id!!, "user@example.com"),
                    ScanElement(id = UUID.randomUUID().toString(), scanData.id!!, "login@example.com"),
                    ScanElement(id = UUID.randomUUID().toString(), scanData.id!!, "mail@example.com")
                )
        every { harvesterAdapter.run(domain) } returns harvesterResults
        every { amassAdapter.run(domain) } returns amassResults

        val result = scanService.runScan(
            domain,
            OutputType.STDOUT,
            ""
        )

        assertEquals(scanData.id, result.id)
        assertEquals(scanData.domain, result.domain)
        assertEquals(scanData.elements!!.size, result.elements!!.size)
        scanData.elements!!.forEachIndexed { index, element ->
            assertEquals(element.id, result.elements!![index].id)
            assertEquals(element.scanId, result.elements!![index].scanId)
            assertEquals(element.value, result.elements!![index].value)
        }
    }

    @Test
    fun partialFailure() = runBlocking {
        val domain = "example.com"
        val scanData = ScanData(
            id = UUID.randomUUID().toString(), domain = domain,
            elements = emptyList()
        )

        val amassResults = listOf("user.example.com")

        every { scanDataRepository.save(any()) } returns scanData
        every { harvesterAdapter.run(domain) } throws RuntimeException("Tool error")
        every { amassAdapter.run(domain) } returns amassResults
        every { scanElementRepository.saveAll(any()) } returns
                listOf(
                    ScanElement(id = UUID.randomUUID().toString(), scanData.id!!, "user@example.com"),
                )

        val result = scanService.runScan(domain, OutputType.STDOUT, "")

        assertEquals(scanData.id, result.id)
        assertEquals(scanData.domain, result.domain)
        assertEquals(scanData.elements!!.size, result.elements!!.size)
        scanData.elements!!.forEachIndexed { index, element ->
            assertEquals(element.id, result.elements!![index].id)
            assertEquals(element.scanId, result.elements!![index].scanId)
            assertEquals(element.value, result.elements!![index].value)
        }
    }

    @Test
    fun bothToolsFailThrowsException() = runBlocking {
        val domain = "example.com"
        val scanData = ScanData(id = UUID.randomUUID().toString(), domain = domain, emptyList())

        every { scanDataRepository.save(any()) } returns scanData
        every { harvesterAdapter.run(domain) } throws RuntimeException("error1")
        every { amassAdapter.run(domain) } throws RuntimeException("error2")
        val exception = kotlin.runCatching { scanService.runScan(domain, OutputType.STDOUT, "") }.exceptionOrNull()

        assertTrue(exception is ToolFailureException)
        assertTrue(exception?.message?.contains("Both tools failed") == true)
    }

    @Test
    fun retrieveScan() = runBlocking {
        val domain = "example.com"
        val scanData = ScanData(id = UUID.randomUUID().toString(), domain = domain, emptyList())

        every { scanDataRepository.findById(scanData.id!!) } returns scanData
        every { scanElementRepository.findAllByScanId(scanData.id!!) } returns
                listOf(
                    ScanElement(id = UUID.randomUUID().toString(), scanData.id!!, "user@example.com"),
                    ScanElement(id = UUID.randomUUID().toString(), scanData.id!!, "login@example.com"),
                    ScanElement(id = UUID.randomUUID().toString(), scanData.id!!, "mail@example.com")
                )
        val result = scanService.retrieveScan(scanData.id!!, OutputType.STDOUT, "")

        assertEquals(scanData.id, result!!.id)
        assertEquals(scanData.domain, result.domain)
        assertEquals(scanData.elements!!.size, result.elements!!.size)
        scanData.elements!!.forEachIndexed { index, element ->
            assertEquals(element.id, result.elements!![index].id)
            assertEquals(element.scanId, result.elements!![index].scanId)
            assertEquals(element.value, result.elements!![index].value)
        }
    }

    @Test
    fun retrieveScanWithWrongId() = runBlocking {
        val domain = "example.com"
        val scanData = ScanData(id = UUID.randomUUID().toString(), domain = domain, emptyList())

        every { scanDataRepository.findById(scanData.id!!) } returns null
        val exception = kotlin.runCatching { scanService.retrieveScan(scanData.id!!, OutputType.STDOUT, "") }.exceptionOrNull()

        assertTrue(exception is IncorrectIdException)
        assertEquals(exception?.message, "No scan found for id: ${scanData.id}")
    }
}