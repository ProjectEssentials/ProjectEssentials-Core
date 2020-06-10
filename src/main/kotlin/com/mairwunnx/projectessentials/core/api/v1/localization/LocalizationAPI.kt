@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION", "RedundantAsync")

package com.mairwunnx.projectessentials.core.api.v1.localization

import com.mairwunnx.projectessentials.core.api.v1.INITIAL_FALLBACK_LANGUAGE
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_FALLBACK_LANG
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.helpers.getResourceAsFile
import com.mairwunnx.projectessentials.core.api.v1.localizationMarker
import com.mairwunnx.projectessentials.core.impl.generalConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager
import org.json.JSONObject
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Localization API class, for interacting with
 * localization.
 * @since 2.0.0-SNAPSHOT.1.
 */
object LocalizationAPI {
    private val logger = LogManager.getLogger()!!
    private val mutex = Mutex()

    private val localizations: MutableMap<String, MutableList<HashMap<String, String>>> =
        Collections.synchronizedMap(mutableMapOf())

    fun apply(clazz: Class<*>, entries: () -> List<String>) {
        CoroutineScope(Dispatchers.Default).launch {
            async {
                entries().asSequence().forEach {
                    val name = it.substring(it.lastIndexOf("/")).drop(1).dropLast(5)
                    measureTimeMillis {
                        val json = getResourceAsFile(
                            clazz.classLoader, it
                        )?.readText() ?: error("Localization $it / $name failed to process")
                        JSONObject(json).also { jsonObject ->
                            jsonObject.keys().asSequence().filter { predicate ->
                                predicate != "_comment"
                            }.forEach { key ->
                                val value = jsonObject.get(key) as String
                                change(name, key, value)
                            }
                        }
                    }.also { time ->
                        logger.debug(
                            localizationMarker, "Localization `$name` processed with ${time}ms"
                        )
                    }
                }
            }.await()
        }
    }

    @Synchronized
    private suspend fun change(name: String, key: String, value: String) {
        mutex.withLock {
            val result = localizations[name]
            if (result == null) {
                localizations[name] = mutableListOf(hashMapOf(key to value))
            } else {
                result.add(hashMapOf(Pair(key, value)))
            }
        }
    }

    /**
     * Applying localization, without (with since 2.0.1) processing. Thread safe.
     *
     * @param localization localization data class instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun apply(localization: Localization) = apply(localization.sourceClass) { localization.sources }

    /**
     * @return fall back localizations language.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getFallBackLanguage() = generalConfiguration.getStringOrDefault(
        SETTING_LOC_FALLBACK_LANG, INITIAL_FALLBACK_LANGUAGE
    )

    /**
     * Install new fall back localizations language.
     * @param language language string in format `xx_xx`.
     * code is illegal.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun setFallBackLanguage(language: String) =
        if (language.matches(Regex("^[a-z]{2}_[a-z]{2}$"))) {
            generalConfiguration.put(SETTING_LOC_FALLBACK_LANG, language)
        } else throw error("Language code format $language incorrect and unsupported.")

    /**
     * @param targetLanguage target language, in format `xx_xx`.
     * @param l10nString minecraft localization string.
     * @param args some arguments for string if provided.
     * @param argumentChar argument char for processing arguments.
     * @return localized string, if localization string
     * not found, then return empty string.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getLocalizedString(
        targetLanguage: String,
        l10nString: String,
        vararg args: String,
        argumentChar: Char = 's'
    ): String {
        var msg = String.empty
        val messagesList = localizations[
                targetLanguage.toLowerCase()
        ] ?: localizations[getFallBackLanguage()]

        messagesList!!.asSequence().forEach {
            it[l10nString]?.let { message -> msg = message }
        }.also {
            for (i in 0 until msg.asSequence().filter { it == '%' }.count()) {
                msg = msg.replaceFirst("%$argumentChar", args[i])
            }
        }

        if (msg.isEmpty()) {
            StringBuilder(msg).apply {
                append("Probably localization error occurred:\n")
                append("    > Requested language: $targetLanguage,\n")
                append("    > Requested string: $l10nString,\n")
                append("    > Fallback language: ${getFallBackLanguage()},\n")
                append("    > Messages count: ${messagesList.count()},\n")
                append("    > Registered localizations: ${localizations.count()}")
            }.toString().also { logger.error(it) }
        }
        return msg
    }

    /**
     * @param player target player to get result.
     * @return player language.
     * @since 2.0.0.
     */
    fun getPlayerLanguage(player: ServerPlayerEntity): String = player.language
}
