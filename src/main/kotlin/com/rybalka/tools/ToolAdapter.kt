package com.rybalka.tools

interface ToolAdapter {
    fun run(domain: String): List<String>
}