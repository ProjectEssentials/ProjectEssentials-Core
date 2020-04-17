@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.configuration


/**
 * Mandatory annotation for configuration classes. Targets
 * on classes.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@OptIn(ExperimentalUnsignedTypes::class)
@MustBeDocumented
annotation class Configuration(
    /**
     * Configuration name, for working with it later.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    val name: String,
    /**
     * Configuration version, for compatibility checking. By
     * default value is 0.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    val version: UInt = 0u
)
