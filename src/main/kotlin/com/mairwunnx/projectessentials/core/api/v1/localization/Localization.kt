package com.mairwunnx.projectessentials.core.api.v1.localization

/**
 * Localization data class, stores
 * mandatory data for processing localizations.
 * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
 */
data class Localization(
    /**
     * Paths to localization files. (Full paths).
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    val sources: MutableList<String>,
    /**
     * Source name for indexing and working with
     * localizations in future.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    val sourceName: String,
    /**
     * Source class (for getting resources relative
     * class).
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    val sourceClass: Class<*>
)
