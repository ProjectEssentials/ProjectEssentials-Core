package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Configuration interface for all configuration
 * in project essentials modules.
 *
 * @param T configuration data class.
 *
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
interface IConfiguration<T> {
    /**
     * Configuration instance (must initialized in
     * `load` method).
     *
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    val configuration: T

    /**
     * Path to configuration.
     *
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    val path: String

    /**
     * Load configuration from local storage or memory.
     * Also initializing configuration field.
     *
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun load()

    /**
     * Save configuration to local storage.
     *
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun save()

    /**
     * @return configuration model instance.
     *
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun take(): T
}
