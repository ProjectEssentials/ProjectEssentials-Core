package com.mairwunnx.projectessentials.core.api.v1.module

/**
 * Base interface for all Project Essentials modules.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
@Suppress("unused")
interface IModule {
    /**
     * Initialize the module, calling main mod functions.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun init()

    /**
     * @return module instance.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getModule(): IModule

    /**
     * @return module meta data as Module annotation class.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getModuleData(): Module

    /**
     * Reload module, configuration and other data.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun reload() = Unit
}
