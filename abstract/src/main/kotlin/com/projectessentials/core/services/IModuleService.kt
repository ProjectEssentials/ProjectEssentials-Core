package com.projectessentials.core.services

import com.projectessentials.core.contracts.IModule
import kotlinx.coroutines.Deferred

/**
 * Module service contract, for interacting with
 * modules, register and doing some stuff with it.
 *
 * @since 3.0.0.
 */
interface IModuleService {
    /**
     * @param module module to check existing.
     * @return `true` if module with specified
     * name is exist otherwise `false`.
     * @since 3.0.0.
     */
    fun isModuleExist(module: String): Boolean

    /**
     * @param module module to get.
     * @return founded module by specified name
     * represented as [IModule], if module with
     * requested name is not exist will return
     * `null`.
     * @since 3.0.0.
     */
    fun getModuleByName(module: String): IModule?

    /**
     * @return sequence of all registered modules.
     * @since 3.0.0.
     */
    fun getModules(): Sequence<IModule>

    /**
     * Do register passed module synchronously.
     * See also [registerAsync] for registering module
     * asynchronously.
     *
     * @param module module class reference to register.
     * @return `true` if module registered successfully,
     * if module already registered will return `false`.
     * @since 3.0.0.
     * @see registerAsync
     */
    fun register(module: IModule): Boolean

    /**
     * Do register passed module asynchronously.
     * See also [register] for registering module
     * synchronously.
     *
     * @param module module class reference to register.
     * @return `true` if module registered successfully,
     * if module already registered will return `false`.
     * @since 3.0.0.
     * @see register
     */
    suspend fun registerAsync(module: IModule): Deferred<Boolean>

    /**
     * Perform reload of target module name. (synchronously
     * reloading).
     * See also [reloadAsync] for reloading asynchronously.
     *
     * @param module module to reload.
     * @since 3.0.0.
     * @see reloadAsync
     */
    fun reload(module: String)

    /**
     * Perform reload of target module name. (asynchronously
     * reloading).
     * See also [reload] for reloading synchronously.
     *
     * @param module module to reload.
     * @since 3.0.0.
     * @see reload
     */
    suspend fun reloadAsync(module: String)

    /**
     * Perform all registered modules reloading. (synchronously
     * reloading).
     * See also [reloadAllAsync] for reloading asynchronously.
     *
     * @since 3.0.0.
     * @see reloadAllAsync
     */
    fun reloadAll()

    /**
     * Perform all registered modules reloading. (asynchronously
     * reloading).
     * See also [reloadAll] for reloading synchronously.
     *
     * @since 3.0.0.
     * @see reloadAll
     */
    suspend fun reloadAllAsync()
}
