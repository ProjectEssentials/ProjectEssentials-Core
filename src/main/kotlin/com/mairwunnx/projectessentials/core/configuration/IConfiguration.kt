package com.mairwunnx.projectessentials.core.configuration

/**
 * Configuration interface for all configuration
 * in project essentials modules.
 *
 * @param T configuration data class.
 *
 * @since 1.14.4-1.3.0
 */
interface IConfiguration<T> {
    /**
     * Configuration instance (must initialized in
     * `load` method).
     *
     * @since 1.14.4-1.3.0
     */
    val configuration: T

    /**
     * Path to configuration.
     *
     * @since 1.14.4-1.3.0
     */
    val path: String

    /**
     * Load configuration from local storage or memory.
     * Also initializing configuration field.
     *
     * @since 1.14.4-1.3.0
     */
    fun load()

    /**
     * Save configuration to local storage.
     *
     * @since 1.14.4-1.3.0
     */
    fun save()

    /**
     * @return configuration model instance.
     *
     * @since 1.14.4-1.3.0
     */
    fun take(): T
}
