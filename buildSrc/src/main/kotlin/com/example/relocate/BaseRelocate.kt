package com.example.relocate

import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

abstract class BaseRelocate(private val extToRemap: String) : TransformAction<BaseRelocate.Parameters> {
    interface Parameters : TransformParameters {
        @get:Input
        val relocations: MapProperty<String, String>
    }

    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val originalRenames = parameters.relocations.get()
        // Store both `/` and `.` seperated rename entries for handling both cases
        val renames = originalRenames + originalRenames.map { (fromName, toName) ->
            fromName.replace('.', '/') to toName.replace('.', '/')
        }.toMap()

        val inputFile = inputArtifact.get().asFile

        val outputFile = outputs.file(inputFile.nameWithoutExtension + "-relocated.jar")
        // Make sure the output is ready
        outputFile.parentFile.mkdirs()
        outputFile.delete()

        FileSystems.newFileSystem(inputFile.toPath(), null).use { inputFs ->
            val outputUri = URI.create("jar:" + outputFile.toURI().toString())
            FileSystems.newFileSystem(outputUri, mapOf("create" to true)).use { outputFs ->
                val inputRoot = inputFs.getPath("/")
                val outputRoot = outputFs.getPath("/")

                relocateDir(inputRoot, outputRoot, renames)
            }
        }
    }

    private fun relocateDir(input: Path, output: Path, renames: Map<String, String>) {
        Files.walk(input).use { stream ->
            for (inputFile in stream) {
                if (!Files.isRegularFile(inputFile)) {
                    continue
                }

                // Get absolute path, but not the leading /
                val inputPath = inputFile.toAbsolutePath().toString().substring(1)
                val outputPath = renames.entries.firstOrNull { inputPath.startsWith(it.key) }?.let { (fromPath, toPath) ->
                    inputPath.replace(fromPath, toPath)
                } ?: inputPath

                val outputFile = output.resolve(outputPath)
                Files.createDirectories(outputFile.parent)

                if (!inputFile.fileName.toString().endsWith(".$extToRemap")) {
                    // Copy all non-class files as-is
                    Files.copy(inputFile, outputFile)
                    continue
                }

                relocateFile(inputFile, outputFile, renames)
            }
        }
    }

    abstract fun relocateFile(input: Path, output: Path, renames: Map<String, String>)
}
