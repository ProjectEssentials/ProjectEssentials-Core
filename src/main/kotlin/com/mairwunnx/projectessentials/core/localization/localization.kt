/**
 * Localization helper class for server-side only localization.
 */
@file:Suppress("DuplicatedCode")

package com.mairwunnx.projectessentials.core.localization

import com.mairwunnx.projectessentials.core.extensions.empty
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.TextComponentUtils.toTextComponent
import org.apache.logging.log4j.LogManager
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


private val logger = LogManager.getLogger()
private var localizations: HashMap<String, MutableList<HashMap<String, String>>> = hashMapOf()

/**
 * Fallback language for localization. Uses when
 * player language not registered in localizations.
 *
 * @since 1.14.4-1.3.0
 */
var fallbackLanguage = "en_us"

/**
 * Send localized message, what contains in localized files.
 *
 * **Server-side only using stable!**
 *
 * @param player server player instance.
 * @param l10nString localized string.
 * @param args additional arguments for localized string `(%s literals)`.
 * @param argumentChar localized string argument char.
 * @since 1.14.4-1.3.0
 */
fun sendMsgV2(
    player: ServerPlayerEntity,
    l10nString: String,
    vararg args: String,
    argumentChar: Char = 's'
) {
    val arg = "%$argumentChar"

    var msg = String.empty
    val messagesList =
        localizations[player.language.toLowerCase()] ?: localizations[fallbackLanguage]
    messagesList?.forEach {
        it[l10nString]?.let { message ->
            msg = message
        }
    }

    val argumentCount = msg.filter { it == '%' }.count()
    for (i in 0 until argumentCount) {
        msg = msg.replaceFirst(arg, args[i])
    }

    player.sendMessage(toTextComponent { msg })
}

/**
 * Call it method in mod entry point method.
 * It doing processing localization files in
 * language directory.
 *
 * @param clazz class what calling this method.
 * @param langPaths in-jar lang files paths.
 *
 * @since 1.14.4-1.3.0
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun processLocalizations(
    clazz: Class<*>,
    langPaths: List<String>
) {
    logger.debug("Processing localization for lang directory by class ${clazz.name}")

    langPaths.forEach {
        val localizationName = it.substring(
            it.lastIndexOf("/")
        ).drop(1).dropLast(5)

        logger.debug("Starting processing localization $localizationName")
        loadLocalization(clazz, localizationName, it)
    }
}

/**
 * Just load localization string in memory.
 *
 * @param clazz class what calling this method.
 * @param localization localization name, e.g `en_us`, `ru_ru`.
 * @param fullPath in-jar full path to localization file.
 *
 * @since 1.14.4-1.3.0
 */
fun loadLocalization(clazz: Class<*>, localization: String, fullPath: String) {
    logger.debug("Loading localization $localization in $fullPath")

    val json = getResourceAsFile(clazz.classLoader, fullPath)?.readText()
        ?: throw KotlinNullPointerException()
    val jsonObject = JSONObject(json)
    val keys = jsonObject.keys()

    while (keys.hasNext()) {
        val key = keys.next() as String
        val value = jsonObject.get(key) as String

        logger.debug("Loaded localization key $key with value $value")

        localizations[localization]?.add(hashMapOf(Pair(key, value)))
            ?: localizations.set(
                localization, mutableListOf(hashMapOf(Pair(key, value)))
            )
    }
}

/**
 * @param player server player instance.
 * @param l10nString localized string.
 * @param args additional arguments for localized string `(%s literals)`.
 * @param argumentChar localized string argument char.
 * @return localized string with processed arguments.
 *
 * @since 1.14.4-1.3.0
 */
fun getLocalizedString(
    player: ServerPlayerEntity,
    l10nString: String,
    vararg args: String,
    argumentChar: Char = 's'
): String {
    val arg = "%$argumentChar"

    var msg = String.empty
    val messagesList =
        localizations[player.language.toLowerCase()] ?: localizations[fallbackLanguage]
    messagesList?.forEach {
        it[l10nString]?.let { message ->
            msg = message
        }
    }

    val argumentCount = msg.filter { it == '%' }.count()
    for (i in 0 until argumentCount) {
        msg = msg.replaceFirst(arg, args[i])
    }
    return msg
}

private fun getResourceAsFile(
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
