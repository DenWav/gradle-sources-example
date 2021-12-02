import com.example.relocate.Relocate
import com.example.relocate.SourceRelocate
import com.example.relocate.RelocationExtension

plugins {
    java
}

val relocation = extensions.create("relocation", RelocationExtension::class)

val artifactType = Attribute.of("artifactType", String::class.java)
val relocated = Attribute.of("relocated", Boolean::class.javaObjectType)

dependencies {
    attributesSchema {
        attribute(relocated)
    }
    artifactTypes.getByName("jar") {
        attributes.attribute(relocated, false)
    }

    registerTransform(Relocate::class) {
        val library = objects.named(Category::class, Category.LIBRARY)
        from.attribute(relocated, false).attribute(artifactType, "jar").attribute(Category.CATEGORY_ATTRIBUTE, library)
        to.attribute(relocated, true).attribute(artifactType, "jar").attribute(Category.CATEGORY_ATTRIBUTE, library)

        parameters.relocations.set(relocation.relocations)
    }

    registerTransform(SourceRelocate::class) {
        val documentation = objects.named(Category::class, Category.DOCUMENTATION)
        val sources = objects.named(DocsType::class, DocsType.SOURCES)
        from.attribute(relocated, false).attribute(artifactType, "jar")
            .attribute(Category.CATEGORY_ATTRIBUTE, documentation).attribute(DocsType.DOCS_TYPE_ATTRIBUTE, sources)
        to.attribute(relocated, true).attribute(artifactType, "jar")
            .attribute(Category.CATEGORY_ATTRIBUTE, documentation).attribute(DocsType.DOCS_TYPE_ATTRIBUTE, sources)

        parameters.relocations.set(relocation.relocations)
    }
}

