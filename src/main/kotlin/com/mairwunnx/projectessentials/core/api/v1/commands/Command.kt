package com.mairwunnx.projectessentials.core.api.v1.commands

/**
 * Mandatory annotation for all commands.
 * It annotation needed for detecting command
 * classes in sources and automatically initialize
 * and register it.
 *
 * Annotation constructor contains some variables
 * for interact with command in future.
 *
 * @param name command name without `/`.
 * @param aliases command aliases as array.
 * @param override override already registered command.
 * If value true then already registered command by
 * other mod will be replaced.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
@Target(AnnotationTarget.CLASS)
annotation class Command(
    /**
     * Command name without `/`.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    val name: String,
    /**
     * Command aliases as array.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    val aliases: Array<String> = [],
    /**
     * Override already command. If value
     * true then already registered command by
     * other mod will be replaced.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    val override: Boolean = false
)
