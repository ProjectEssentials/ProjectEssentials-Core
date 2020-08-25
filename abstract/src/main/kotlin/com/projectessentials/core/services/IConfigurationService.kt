package com.projectessentials.core.services

import com.projectessentials.core.contracts.IConfiguration
import kotlinx.coroutines.Deferred

/**
 * Configuration service contract, for interacting
 * with configurations.
 *
 * @since 3.0.0.
 */
interface IConfigurationService {
    /**
     * Disposes all configurations, unloads and just removes
     * from collection of registered configurations.
     *
     * @since 3.0.0.
     */
    fun dispose()

    /**
     * @param configuration configuration name to search and get.
     * @return configuration represented as generic [IConfiguration]
     * with type `*` or null if configuration not exist.
     * @since 3.0.0.
     */
    fun getConfiguration(configuration: String): IConfiguration<*>?

    /**
     * @param T generic type of configuration data class.
     * @param configuration configuration class reference to search it.
     * @return configuration represented as generic [IConfiguration]
     * with type [T] or null if configuration not exist.
     * @since 3.0.0.
     */
    fun <T> getConfiguration(configuration: Class<T>): IConfiguration<T>?

    /**
     * @return sequence of all registered configurations.
     * @since 3.0.0.
     */
    fun getConfigurations(): Sequence<IConfiguration<*>>

    /**
     * Do register passed configuration synchronously.
     * See also [registerAsync] for registering configuration
     * asynchronously.
     *
     * @param configuration configuration class reference to register.
     * @return `true` if configuration registered successfully,
     * if configuration already registered will return `false`.
     * @since 3.0.0.
     * @see registerAsync
     */
    fun register(configuration: IConfiguration<*>): Boolean

    /**
     * Do register passed configuration asynchronously.
     * See also [register] for registering configuration
     * synchronously.
     *
     * @param configuration configuration class reference to register.
     * @return `true` if configuration registered successfully,
     * if configuration already registered will return `false`.
     * @since 3.0.0.
     * @see register
     */
    suspend fun registerAsync(configuration: IConfiguration<*>): Deferred<Boolean>

    /**
     * Perform reload of target configuration name. (synchronously
     * reloading).
     * See also [reloadAsync] for reloading asynchronously.
     *
     * @param configuration configuration to reload.
     * @since 3.0.0.
     * @see reloadAsync
     */
    fun reload(configuration: String)

    /**
     * Perform reload of target configuration name. (asynchronously
     * reloading).
     * See also [reload] for reloading synchronously.
     *
     * @param configuration configuration to reload.
     * @since 3.0.0.
     * @see reload
     */
    suspend fun reloadAsync(configuration: String)

    /**
     * Perform all registered configurations reloading. (synchronously
     * reloading).
     * See also [reloadAllAsync] for reloading asynchronously.
     *
     * @since 3.0.0.
     * @see reloadAllAsync
     */
    fun reloadAll()

    /**
     * Perform all registered configurations reloading. (asynchronously
     * reloading).
     * See also [reloadAll] for reloading synchronously.
     *
     * @since 3.0.0.
     * @see reloadAll
     */
    suspend fun reloadAllAsync()
}
