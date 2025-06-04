package com.rybalka.tools

import com.rybalka.filter.ResultFilter
import com.rybalka.model.ScanElement
import com.rybalka.util.ToolFailureException

class AmassAdapter : ToolAdapter {
    override fun run(domain: String): List<String> {
        val process = ProcessBuilder("docker", "run", "--rm", "caffix/amass", "enum", "-d", domain)
            .start()
        val output = StringBuilder()
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                output.appendLine(line)
            }
        }
        val code = process.waitFor()
        if (code != 0) {
            throw ToolFailureException("Amass process failed with code $code")
        }
        return output.lines()
    }
}