@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.configuration


/**
 * Mandatory annotation for configuration classes. Targets
 * on classes.
 * @since 2.0.0-SNAPSHOT.1.
 */
@Target(AnnotationTarget.CLASS)
@OptIn(ExperimentalUnsignedTypes::class)
@MustBeDocumented
annotation class Configuration(
    /**
     * Configuration name, for working with it later.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val name: String,
    /**
     * Configuration version, for compatibility checking. By
     * default value is 0.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val version: UInt = 0u
)
