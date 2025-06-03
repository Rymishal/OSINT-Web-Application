package com.rybalka.service

import com.rybalka.model.ScanData
import com.rybalka.model.ScanElement
import com.rybalka.repository.ScanDataRepository
import com.rybalka.repository.ScanElementRepository
import com.rybalka.tools.AmassAdapter
import com.rybalka.tools.HarvesterAdapter
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
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
 fun `test successful scan returns id`() = runBlocking {
  val domain = "example.com"
  val scanData = ScanData(
      id = UUID.randomUUID().toString(), domain = domain,
      elements = emptyList()
  )

  val harvesterResults = listOf("user@example.com", "login.example.com")
  val amassResults = listOf("mail.example.com", "login.example.com")

  every { scanDataRepository.save(any()) } returns scanData
  every { harvesterAdapter.run(domain) } returns harvesterResults
  every { amassAdapter.run(domain) } returns amassResults

  val result = scanService.runScan(
      domain,
      OutputType.STDOUT,
      ""
  )

  assertEquals(scanData.id, result)
 }

// @Test
// fun `test partial failure still returns id`() {
//  val domain = "example.com"
//  val scanData = ScanData(
//   id = UUID.randomUUID().toString(), domain = domain,
//   elements = emptyList()
//  )
//
//  val harvesterResults = emptyList<String>()
//  val amassResults = listOf("a.example.com")
//
//
//  every { scanDataRepository.save(any()) } returns scanData
//  every { harvesterAdapter.run(domain) } throws RuntimeException("Tool error")
//  every { amassAdapter.run(domain) } returns amassResults
//
//  every { resultFilter.filter(amassResults, scanData.id!!) } returns filteredAmass
//  every { scanElementRepository.saveAll(filteredAmass) } returns filteredAmass
//
//  val result = scanService.runScan(domain)
//
//  assertEquals(scanData.id, result)
// }
//
// @Test
// fun `test both tools failing throws exception`() {
//  val domain = "example.com"
//  val scanData = ScanData(id = UUID.randomUUID(), domain = domain)
//
//  every { scanDataRepository.save(any()) } returns scanData
//  every { harvesterAdapter.run(domain) } throws RuntimeException("error1")
//  every { amassAdapter.run(domain) } throws RuntimeException("error2")
//
//  val exception = kotlin.runCatching { scanService.runScan(domain) }.exceptionOrNull()
//
//  assertTrue(exception is IllegalStateException)
//  assertTrue(exception?.message?.contains("Both tools failed") == true)
// }
}