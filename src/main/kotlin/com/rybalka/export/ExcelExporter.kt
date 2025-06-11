package com.rybalka.export

import com.rybalka.model.ScanData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.IOException

class ExcelExporter(private val fileName: String) : ResultExporter {
    private val logger = KotlinLogging.logger {}
    override fun export(scanData: ScanData) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Scan-${scanData.id} ${scanData.domain}")

            scanData.elements!!.forEachIndexed { index, item ->
                val row = sheet.createRow(index)
                row.createCell(0).setCellValue(scanData.id)
                row.createCell(1).setCellValue(item.id)
                row.createCell(2).setCellValue(item.value)
            }
            val file = File(fileName)
            workbook.write(file.outputStream())
            workbook.close()
            logger.info { "Excel exported to: ${file.absolutePath}" }
        } catch (e: IOException) {
            logger.error { """"Excel exported to: fileName failed 
                ${e.message} """
            }
        }


    }
}