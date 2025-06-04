package com.rybalka.tools

class AmassStrategy: ToolStrategy {
    override fun createProcess(domain: String): ProcessBuilder {
        return ProcessBuilder("docker", "run", "--rm", "caffix/amass", "enum", "-d", domain)
    }

    override fun getName(): String {
        return "Amass"
    }
}