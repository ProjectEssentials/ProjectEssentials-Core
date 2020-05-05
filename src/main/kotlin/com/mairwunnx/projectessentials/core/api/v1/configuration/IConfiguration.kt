package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Configuration interface for all configuration
 * in project essentials modules.
 *
 * @param T configuration data class.
 *
 * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
 */
@Suppress("unused")
interface IConfiguration<T> {
    /**
     * Configuration instance (must initialized in
     * `load` method).
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    val configuration: T

    /**
     * Path to configuration.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    val path: String

    /**
     * Load configuration from local storage or memory.
     * Also initializing configuration field.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun load()

    /**
     * Save configuration to local storage.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun save()

    /**
     * @return configuration model instance.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun take(): T

    /**
     * @return configuration meta data as Configuration annotation class.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun data(): Configuration
}
