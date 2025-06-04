package com.rybalka.tools

import com.rybalka.util.ToolFailureException

class HarvesterAdapter
    : ToolAdapter {
    override fun run(domain: String): List<String> {
        val process = ProcessBuilder("docker", "run", "--rm", "simonthomas/theharvester", "theharvester", "-d", domain, "-b", "bing")
            .redirectErrorStream(true)
            .start()
        val output = StringBuilder()
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                output.appendLine(line)
            }
        }
        val code = process.waitFor()
        if (code != 0) {
            throw ToolFailureException("Harvester process failed with code $code")
        }
        return output.lines()
    }
}