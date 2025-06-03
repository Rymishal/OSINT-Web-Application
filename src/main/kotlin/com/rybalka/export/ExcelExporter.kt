package com.rybalka.export

import com.rybalka.model.ScanData
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExcelExporter(private val fileName: String) : ResultExporter {
    override fun export(scanData: ScanData) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Scan-${scanData.id} ${scanData.domain}")

        scanData.elements!!.forEachIndexed { index, item ->
            val row = sheet.createRow(index)
            row.createCell(0).setCellValue(item.id)
            row.createCell(1).setCellValue(item.value)
        }
        val file = File(fileName)
        workbook.write(file.outputStream())
        workbook.close()
        println("Excel exported to: ${file.absolutePath}")
    }
}