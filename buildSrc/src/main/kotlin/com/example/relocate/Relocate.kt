package com.example.relocate

import org.gradle.api.artifacts.transform.CacheableTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import java.nio.file.Files
import java.nio.file.Path

@CacheableTransform
abstract class Relocate : BaseRelocate("class") {
    override fun relocateFile(input: Path, output: Path, renames: Map<String, String>) {
        val inputData = Files.readAllBytes(input)
        val reader = ClassReader(inputData)
        val writer = ClassWriter(0)
        val remapper = ClassRemapper(writer, SimpleRemapper(renames))

        reader.accept(remapper, 0)

        Files.write(output, writer.toByteArray())
    }
}
