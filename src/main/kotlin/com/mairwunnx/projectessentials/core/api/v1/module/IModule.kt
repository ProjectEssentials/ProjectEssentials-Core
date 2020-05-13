package com.mairwunnx.projectessentials.core.api.v1.module

/**
 * Base interface for all Project Essentials modules.
 * @since 2.0.0-SNAPSHOT.1.
 */
interface IModule {
    /**
     * Module name, for example `core` or `basic`.
     * @since 2.0.0-RC.2.
     */
    val name: String

    /**
     * Module version, for example `1.0.0-SNAPSHOT.1+<meta-data>`, or
     * `1.15.2-1.606.31.91`.
     *
     * @since 2.0.0-RC.2.
     */
    val version: String

    /**
     * Loading index, can't contains two and more
     * modules with same load index.
     *
     * Affects on loading order, for example:
     *
     *  > 0 - first to load. 100 - last to load.
     *
     * @since 2.0.0-RC.2.
     */
    val loadIndex: Int

    /**
     * Initialize the module, calling main mod functions.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun init()
}
