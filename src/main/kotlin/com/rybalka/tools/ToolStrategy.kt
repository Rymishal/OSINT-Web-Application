package com.rybalka.tools

interface ToolStrategy {
    fun createProcess(domain: String): ProcessBuilder
    fun getName(): String
}