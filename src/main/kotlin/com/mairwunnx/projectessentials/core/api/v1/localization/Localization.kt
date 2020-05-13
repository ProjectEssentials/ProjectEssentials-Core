package com.mairwunnx.projectessentials.core.api.v1.localization

/**
 * Localization data class, stores
 * mandatory data for processing localizations.
 * @since 2.0.0-SNAPSHOT.1.
 */
data class Localization(
    /**
     * Paths to localization files. (Full paths).
     * @since 2.0.0-SNAPSHOT.1.
     */
    val sources: MutableList<String>,
    /**
     * Source name for indexing and working with
     * localizations in future.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val sourceName: String,
    /**
     * Source class (for getting resources relative
     * class).
     * @since 2.0.0-SNAPSHOT.1.
     */
    val sourceClass: Class<*>
)
