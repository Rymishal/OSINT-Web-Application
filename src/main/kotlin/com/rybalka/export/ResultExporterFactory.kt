package com.rybalka.export

import com.rybalka.service.OutputType

class ResultExporterFactory {
    companion object {
        fun createExporter(type: OutputType, filename: String): ResultExporter {

            return when (type) {
                OutputType.STDOUT -> StdoutExporter()
                OutputType.EXCEL -> ExcelExporter(filename)
            }
        }
    }
}