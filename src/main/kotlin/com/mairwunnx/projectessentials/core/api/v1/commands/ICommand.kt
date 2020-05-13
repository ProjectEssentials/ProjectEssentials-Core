package com.mairwunnx.projectessentials.core.api.v1.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

/**
 * Base interface for all command classes.
 * @since 2.0.0-SNAPSHOT.1.
 */
interface ICommand {
    /**
     * Command name without `/`.
     * @since 2.0.0-RC.2.
     */
    val name: String

    /**
     * Command aliases as array.
     * @since 2.0.0-RC.2.
     */
    val aliases: List<String>

    /**
     * Override already command. If value
     * true then already registered command by
     * other mod will be replaced.
     * @since 2.0.0-RC.2.
     */
    val override: Boolean

    /**
     * Initialize command, assign data and other.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun initialize()

    /**
     * Register command.
     * @param dispatcher command dispatcher.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun register(dispatcher: CommandDispatcher<CommandSource>)

    /**
     * Process command, i.e execute command.
     * @param context command context.
     * @return int. Command execution result.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun process(context: CommandContext<CommandSource>): Int
}
