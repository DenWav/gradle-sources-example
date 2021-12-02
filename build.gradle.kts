plugins {
    java
    relocate
}

repositories {
    mavenCentral()
}

relocation {
    relocations.put("com.google.gson", "libs.com.google.gson")
}
val relocated = Attribute.of("relocated", Boolean::class.javaObjectType)

dependencies {
    implementation("com.google.code.gson:gson:2.8.9") {
        attributes {
            attribute(relocated, true)
        }
    }
}

tasks.register("resolveCompileSources", Task::class) {
    doLast {
        val componentIds = configurations.compileClasspath.get().incoming.artifacts.map { it.id.componentIdentifier }

        val result = dependencies.createArtifactResolutionQuery()
            .forComponents(componentIds)
            .withArtifacts(JvmLibrary::class, SourcesArtifact::class)
            .execute()

        for (component in result.resolvedComponents.sortedBy { it.id.toString() }) {
            component.getArtifacts(SourcesArtifact::class)
                .forEach { println("Source artifact for ${component.id}: ${(it as ResolvedArtifactResult).file}") }
        }
    }
}
