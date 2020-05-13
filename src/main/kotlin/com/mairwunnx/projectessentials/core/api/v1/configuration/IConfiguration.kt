package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Configuration interface for all configuration
 * in project essentials modules.
 *
 * @param T configuration data class.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused")
interface IConfiguration<T> {
    /**
     * Configuration instance (must initialized in
     * `load` method).
     * @since 2.0.0-SNAPSHOT.1.
     */
    val configuration: T

    /**
     * Path to configuration.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val path: String

    /**
     * Load configuration from local storage or memory.
     * Also initializing configuration field.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun load()

    /**
     * Save configuration to local storage.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun save()

    /**
     * @return configuration model instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun take(): T

    /**
     * @return configuration meta data as Configuration annotation class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun data(): Configuration
}
