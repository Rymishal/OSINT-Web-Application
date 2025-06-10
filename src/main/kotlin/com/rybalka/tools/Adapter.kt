package com.rybalka.tools

import com.rybalka.util.ToolFailureException

class Adapter(private val strategy: ToolStrategy) {
    fun run(domain: String): List<String> {
        val process = strategy.createProcess(domain)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val code = process.waitFor()
        if (code != 0) {
            throw ToolFailureException("${strategy.getName()} process failed with code $code")
        }
        return output.lines()
    }
}