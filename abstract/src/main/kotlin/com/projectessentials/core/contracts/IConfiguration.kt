package com.projectessentials.core.contracts

/**
 * Contract for all configurations of project essentials,
 * for interacting with it see also [com.projectessentials.core.services.IConfigurationService].
 *
 * @param T generic type of configuration data class.
 * @property configuration configuration state.
 * @property path path to configuration file.
 * @property name name of configuration to search later it.
 * @since 3.0.0.
 */
interface IConfiguration<T> {
    val configuration: T
    val path: String
    val name: String

    /**
     * Performs configuration loading action.
     *
     * @since 3.0.0.
     */
    fun load()

    /**
     * Performs configuration saving action.
     *
     * @since 3.0.0.
     */
    fun save()

    /**
     * Performs configuration reloading action.
     *
     * If [save] argument value is true will do:
     *
     * 1. Saves configuration to disk.
     * 2. Loading configuration again.
     *
     * Otherwise:
     *
     * 1. Loading configuration again.
     *
     * @param save save before reloading? (default
     * value is `true`).
     * @since 3.0.0.
     */
    fun reload(save: Boolean = true)
}
