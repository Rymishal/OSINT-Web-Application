package com.rybalka.tools

import com.rybalka.util.ToolFailureException

class HarvesterAdapter
    : ToolAdapter {
    override fun run(domain: String): List<String> {
        val process = ProcessBuilder("docker", "exec", "theharvester", "theharvester", "-d", domain, "-b", "bing")
            .start()
        val code = process.waitFor()
        val out = process.inputStream.bufferedReader().readLines()
        if (code != 0) {
            throw ToolFailureException("Harvester process failed with code $code")
        }
        return out
    }
}