@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Mandatory annotation for configuration classes. Targets
 * on classes.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
@Target(AnnotationTarget.CLASS)
annotation class Configuration(
    /**
     * Configuration name, for working with it later.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    val name: String
)
