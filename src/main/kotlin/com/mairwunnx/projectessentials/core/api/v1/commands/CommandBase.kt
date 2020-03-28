package com.mairwunnx.projectessentials.core.api.v1.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import org.apache.logging.log4j.LogManager

/**
 * Base abstract class for commands. Has common
 * logic for registering commands.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
abstract class CommandBase(
    /**
     * Command literal for working with command.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    val literal: LiteralArgumentBuilder<CommandSource>
) : ICommand {
    private val logger = LogManager.getLogger()

    /**
     * Command data, stores data of `Command`
     * annotation type.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    lateinit var data: Command

    /**
     * Initializing command. For this case, just
     * remove already registered command if needed.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    override fun initialize() {
        if (data.override) {
            CommandAPI.removeCommand(data.name)
        }
    }

    /**
     * Register command.
     * @param dispatcher command dispatcher.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        if (ModuleAPI.isModuleExist("cooldown")) {
            CommandsAliases.aliases[data.name] = data.aliases.toMutableList()
        }

        val literalNode =
            dispatcher.register(literal.executes(::process))

        data.aliases.forEach {
            if (it != data.name) {
                dispatcher.register(
                    Commands.literal(it).executes(::process).redirect(literalNode)
                )
            }
        }
    }

    /**
     * Process command, i.e execute command.
     * @param context command context.
     * @return int. Command execution result.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    override fun process(context: CommandContext<CommandSource>): Int {
        logger.debug(
            " :: Executed command ${context.input} by ${context.playerName()}"
        )
        return 0
    }

    /**
     * @param clazz from what need take data.
     * @return Command annotation data class.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    override fun getData(clazz: Class<*>): Command =
        clazz.getAnnotation(Command::class.java)
}
