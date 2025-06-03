package com.rybalka.tools

import com.rybalka.filter.ResultFilter

class ToolStrategy {
    fun createAll(domain: String, scanId: String): List<ToolAdapter> =
        listOf(
            AmassAdapter(),
            HarvesterAdapter()
        )
}