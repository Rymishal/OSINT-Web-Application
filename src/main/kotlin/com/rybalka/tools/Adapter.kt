package com.rybalka.tools

import com.rybalka.util.ToolFailureException

class Adapter(private val strategy: ToolStrategy) {
    fun run(domain: String): List<String> {
        val process = strategy.createProcess(domain)
            .start()
        val output = StringBuilder()
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                output.appendLine(line)
            }
        }
        val code = process.waitFor()
        if (code != 0) {
            throw ToolFailureException("${strategy.getName()} process failed with code $code")
        }
        return output.lines()
    }
}