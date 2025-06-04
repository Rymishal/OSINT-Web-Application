package com.rybalka.tools

class HarvesterStrategy: ToolStrategy {
    override fun createProcess(domain: String): ProcessBuilder {
        return ProcessBuilder("docker", "run", "--rm", "simonthomas/theharvester", "theharvester", "-d", domain, "-b", "bing")
    }

    override fun getName(): String {
        return "Harvester"
    }
}