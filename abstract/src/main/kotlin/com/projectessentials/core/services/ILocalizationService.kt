package com.projectessentials.core.services

import kotlinx.coroutines.Deferred

/**
 * Localization service contract, for interacting
 * with localization.
 *
 * @property fallbackLanguage fallback language if
 * no one string found for target language.
 * @since 3.0.0.
 */
interface ILocalizationService {
    val fallbackLanguage: String

    /**
     * Do process asynchronously passed entries with paths to
     * localization files and relative class.
     *
     * @param clazz class of module wants to process his strings.
     * @param entries sequence of strings with paths to
     * localization files.
     * @since 3.0.0.
     */
    fun process(clazz: Class<*>, entries: () -> Sequence<String>)

    /**
     * See also [stringOfAsync] if you need to get string asynchronously.
     *
     * @param l10nString localization string to get string.
     * @param language target language to get, if value not passed
     * will used default language in [fallbackLanguage], if default
     * language is incorrect will used `en_us` localization.
     * @param args arguments to replace `%s` in the string.
     * @return localized string with target localization.
     * @see stringOfAsync
     * @since 3.0.0.
     */
    fun stringOf(
        l10nString: String,
        language: String = fallbackLanguage,
        vararg args: Any
    ): String

    /**
     * See also [stringOf] if you need to get string synchronously.
     *
     * @param l10nString localization string to get string.
     * @param language target language to get, if value not passed
     * will used default language in [fallbackLanguage], if default
     * language is incorrect will used `en_us` localization.
     * @param args arguments to replace `%s` in the string.
     * @return deferred with value represented as localized
     * string with target localization. (asynchronously).
     * @see stringOf
     * @since 3.0.0.
     */
    suspend fun stringOfAsync(
        l10nString: String,
        language: String = fallbackLanguage,
        vararg args: Any
    ): Deferred<String>
}
