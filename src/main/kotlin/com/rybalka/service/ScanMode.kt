package com.rybalka.service

enum class OutputType { STDOUT, EXCEL }

sealed class ScanMode {
    data class Scan(val domain: String, val outputType: OutputType, val filename: String) : ScanMode()
    data class Retrieve(val id: String, val outputType: OutputType, val filename: String) : ScanMode()

    companion object {
        fun parse(args: Array<String>): ScanMode {
            val mode = if (args.any { it == "--retrieve" }) "retrieve" else "scan"
            val output = if (args.contains("-o")) args[args.indexOf("-o") + 1] else throw Exception("Output parameter not found")
            val filename = if (OutputType.EXCEL.name.equals(output, ignoreCase = true)) args[args.indexOf("-o") + 2] else ""
            return when (mode) {
                "retrieve" -> Retrieve(args.last(), OutputType.valueOf(output.uppercase()), filename)
                else -> Scan(args.last(), OutputType.valueOf(output.uppercase()), filename)
            }
        }
    }
}