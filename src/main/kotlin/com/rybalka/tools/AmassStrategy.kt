package com.rybalka.tools

class AmassStrategy: ToolStrategy {
    override fun createProcess(domain: String): ProcessBuilder {
        return ProcessBuilder(
            "docker", "run", "--rm", "caffix/amass", "enum",
            "-d", domain, "-passive", "-timeout", "30", "-max-dns-queries", "100")
    }

    override fun getName(): String {
        return "Amass"
    }
}