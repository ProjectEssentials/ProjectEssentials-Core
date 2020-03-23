package com.mairwunnx.projectessentials.core.api.v1.helpers

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @param classLoader class loaded.
 * @param resourcePath path to resource file.
 * @return resource file as file. Return null
 * if file not exist or if was throw `IOException`.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun getResourceAsFile(
    classLoader: ClassLoader, resourcePath: String
): File? {
    return try {
        val inputStream = classLoader.getResourceAsStream(
            resourcePath
        ) ?: return null

        val tempFile = File.createTempFile(
            java.lang.String.valueOf(inputStream.hashCode()), ".tmp"
        )
        tempFile.deleteOnExit()

        FileOutputStream(tempFile).use { out ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                out.write(buffer, 0, bytesRead)
            }
        }
        tempFile
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

/**
 * @return absolutely path to minecraft client or server root dir.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun getRootDirectory(): String = File(".").absolutePath

/**
 * Common json instance with default configuration
 * for Project Essentials modules, if module using
 * json configuration, then you need use this property.
 * @since 1.14.4-1.0.3.2 / Mod: 1.14.4-2.0.0, API: 1.0.0
 */
@OptIn(kotlinx.serialization.UnstableDefault::class)
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

/**
 * Minecraft config folder absolutely path.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
val configDirectory = getRootDirectory() + File.separator + "config"

/**
 * Project Essentials mod config folder.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
val projectConfigDirectory = configDirectory + File.separator + "ProjectEssentials"
