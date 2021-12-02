package com.example.relocate

import org.objectweb.asm.commons.Remapper;

class SimpleRemapper(private val renames: Map<String, String>) : Remapper() {
    override fun map(internalName: String): String {
        for ((fromName, toName) in renames.entries) {
            if (internalName.startsWith(fromName)) {
                return internalName.replaceFirst(fromName, toName)
            }
        }
        return internalName
    }

    override fun mapValue(value: Any?): Any {
        if (value !is String) {
            return super.mapValue(value)
        }

        for ((fromName, toName) in renames.entries) {
            if (value.startsWith(fromName)) {
                return value.replaceFirst(fromName, toName)
            }
        }

        return value
    }
}
