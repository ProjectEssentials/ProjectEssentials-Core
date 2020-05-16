package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Configuration interface for all configuration
 * in project essentials modules.
 *
 * @param T configuration data class.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
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
     * Configuration name, for working with it later.
     * @since 2.0.0-RC.2.
     */
    val name: String

    /**
     * Configuration version, for compatibility checking. By
     * default value is 0.
     * @since 2.0.0-RC.2.
     */
    val version: Int

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
}
