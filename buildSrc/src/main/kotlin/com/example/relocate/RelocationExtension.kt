package com.example.relocate

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.kotlin.dsl.mapProperty

open class RelocationExtension(objects: ObjectFactory) {

    val relocations: MapProperty<String, String> = objects.mapProperty()
}
