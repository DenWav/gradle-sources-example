package com.example.relocate

import org.gradle.api.artifacts.transform.CacheableTransform
import java.nio.file.Files
import java.nio.file.Path

@CacheableTransform
abstract class SourceRelocate : BaseRelocate("java") {
    override fun relocateFile(input: Path, output: Path, renames: Map<String, String>) {
        Files.newBufferedWriter(output).use { writer ->
            Files.newBufferedReader(input).forEachLine { line ->
                writer.append(relocateLine(line, renames)).appendLine()
            }
        }
    }

    private fun relocateLine(line: String, renames: Map<String, String>): String {
        var currentLine = line
        for ((fromPackage, toPackage) in renames.entries) {
            currentLine = currentLine.replace(fromPackage, toPackage)
        }
        return currentLine
    }
}
