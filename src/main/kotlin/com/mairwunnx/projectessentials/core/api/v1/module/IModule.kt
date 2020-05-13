package com.mairwunnx.projectessentials.core.api.v1.module

/**
 * Base interface for all Project Essentials modules.
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused")
interface IModule {
    /**
     * Initialize the module, calling main mod functions.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun init()

    /**
     * @return module instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getModule(): IModule

    /**
     * @return module meta data as Module annotation class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getModuleData(): Module

    /**
     * Reload module, configuration and other data.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun reload() = Unit
}
