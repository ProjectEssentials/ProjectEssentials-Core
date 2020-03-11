@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.helpers

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

/**
 * Common json instance with default configuration
 * for Project Essentials modules, if module using
 * json configuration, then you need use this property.
 * @since 1.14.4-1.0.3.2
 */
val jsonInstance = Json(
    JsonConfiguration(
        encodeDefaults = true,
        ignoreUnknownKeys = true,
        isLenient = false,
        serializeSpecialFloatingPointValues = false,
        allowStructuredMapKeys = true,
        prettyPrint = true,
        unquotedPrint = false,
        useArrayPolymorphism = false
    )
)
