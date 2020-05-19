@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.localization

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationProcessor.fallbackLanguage
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * Localization API class, for interacting with
 * localization.
 * @since 2.0.0-SNAPSHOT.1.
 */
object LocalizationAPI {
    /**
     * Applying localization, without processing. Thread safe.
     * **Apply only in setup event!**
     * @param localization localization data class instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun apply(localization: Localization) {
        synchronized(this) {
            LocalizationProcessor.localizations.add(localization)
        }
    }

    /**
     * Removing localization from localizations. (For
     * applying changes you need remove all localization data
     * and re-process localization processor).
     * @param localization localization data class with specified
     * name and sources.
     * @return true if any elements were removed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun remove(localization: Localization) =
        LocalizationProcessor.localizations.removeIf {
            it.sourceName == localization.sourceName && it.sources == localization.sources
        }

    /**
     * Removing all localizations.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun removeAll() = LocalizationProcessor.localizations.clear()

    /**
     * @return fall back localizations language.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getFallBackLanguage() = fallbackLanguage

    /**
     * Install new fall back localizations language.
     * @param language language string in format `xx_xx`.
     * @throws IllegalLanguageCodeException when language
     * code is illegal.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun setFallBackLanguage(language: String) =
        if (language.matches(Regex("^[a-z]{2}_[a-z]{2}$"))) {
            fallbackLanguage = language
        } else {
            throw IllegalLanguageCodeException(
                "Language code format $language incorrect and unsupported."
            )
        }

    /**
     * @param name target localization name.
     * @return Localization data by specified name.
     * @throws LocalizationNotFoundException when localization
     * not found.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getByName(name: String) =
        LocalizationProcessor.localizations.find {
            it.sourceName == name
        } ?: throw LocalizationNotFoundException(
            "Localization with name $name not found."
        )

    /**
     * @return all localizations as mutable list.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getAll() = LocalizationProcessor.localizations

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
        val messagesList = LocalizationProcessor.localizationsData[
                targetLanguage.toLowerCase()
        ] ?: LocalizationProcessor.localizationsData[fallbackLanguage]

        messagesList?.forEach {
            it[l10nString]?.let { message ->
                msg = message
            }
        }

        for (i in 0 until msg.filter { it == '%' }.count()) {
            msg = msg.replaceFirst("%$argumentChar", args[i])
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
