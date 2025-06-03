package com.rybalka.filter

import com.rybalka.model.ScanElement
import java.util.stream.Collectors

class ResultFilter(private val regex: Regex) {
    fun filter(lines: List<String>, scanId: String): Set<ScanElement> {
        return lines.stream().filter{ it.matches(regex) }
            .map { ScanElement(null, scanId, it) }.collect(Collectors.toSet())
    }

}