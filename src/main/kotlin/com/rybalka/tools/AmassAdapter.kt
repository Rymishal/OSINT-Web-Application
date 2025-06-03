package com.rybalka.tools

import com.rybalka.filter.ResultFilter
import com.rybalka.model.ScanElement
import com.rybalka.util.ToolFailureException

class AmassAdapter : ToolAdapter {
    override fun run(domain: String): List<String> {
        val process = ProcessBuilder("docker", "exec", "amass", "amass", "enum", "-d", domain)
            .start()
        val code = process.waitFor()
        val out = process.inputStream.bufferedReader().readLines()
        if (code != 0) {
            throw ToolFailureException("Amass processzz failed with code $code")
        }
        return out
    }
}