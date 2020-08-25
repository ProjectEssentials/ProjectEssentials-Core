package com.projectessentials.core.contracts

/**
 * Contract for all modules of project essentials,
 * for interacting with it see also [com.projectessentials.core.services.IModuleService].
 *
 * @since 3.0.0.
 * @see com.projectessentials.core.services.IModuleService
 */
interface IModule {
    /**
     * Called when module starting initializing. (by
     * module system of course).
     *
     * @since 3.0.0.
     */
    fun onInitializing()

    /**
     * Called when module starting reloading.
     *
     * @since 3.0.0.
     */
    fun onReloading()

    /**
     * Called when module starting registering.
     *
     * @since 3.0.0.
     */
    fun onRegistering()

    /**
     * Called when module was registered successfully.
     *
     * @since 3.0.0.
     */
    fun onRegistered()

    /**
     * Called when requested module reloading.
     *
     * @param args arguments for reloading module.
     * @since 3.0.0.
     */
    fun reload(vararg args: String)
}
