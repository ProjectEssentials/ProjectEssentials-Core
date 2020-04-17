@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.module


/**
 * Mandatory annotation for all modules. Targets
 * on classes.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@OptIn(ExperimentalUnsignedTypes::class)

@MustBeDocumented
annotation class Module(
    /**
     * Module name, for example `Project Essentials` or
     * `Project Essentials Auth`.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    val name: String,
    /**
     * Module version, for example `1.14.4-1.0.0`, or
     * `1.15.2-1.606.31.91`.
     *
     * **Must starts with minecraft version.**
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    val version: String,
    /**
     * Loading index, can't contains two and more
     * modules with same load index.
     *
     * Affects on loading order, for example:
     *
     *  > 0 - first to load. 100 - last to load.
     *
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    val loadIndex: UInt,
    /**
     * Module API version if API provided. For example
     * `1.3.3`, `1.0.91.1`.
     *
     * **Must not include minecraft version, only API version.**
     *
     * By default value is "not provided", and if API not provided,
     * then leave this empty.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    val apiVersion: String = "not provided"
)
