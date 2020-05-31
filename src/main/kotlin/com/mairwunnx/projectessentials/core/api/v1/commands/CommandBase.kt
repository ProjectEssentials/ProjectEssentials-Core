package com.mairwunnx.projectessentials.core.api.v1.commands


import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

/**
 * Base abstract class for commands. Has common
 * logic for registering commands.
 * @since 2.0.0-SNAPSHOT.1.
 */
abstract class CommandBase(
    /**
     * Command literal for working with command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    var literal: LiteralArgumentBuilder<CommandSource>,
    /**
     * If value true then action for command will added
     * automatically with reference on method [process].
     * @since 2.0.0-SNAPSHOT.1.
     */
    val actionNeed: Boolean = true
) : ICommand {
    private val marker = MarkerManager.Log4jMarker("COMMAND OUT")
    private val logger = LogManager.getLogger()

    abstract override val name: String
    override val aliases: List<String> = emptyList()
    override val override: Boolean = false

    /**
     * Initializing command. For this case, just
     * remove already registered command if needed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    override fun initialize() {
        if (override) CommandAPI.removeCommand(name)
    }

    /**
     * Register command.
     * @param dispatcher command dispatcher.
     * @since 2.0.0-SNAPSHOT.1.
     */
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        fun hack(value: String) = try {
            with(literal.javaClass.getDeclaredField("literal")) {
                isAccessible = true
                set(literal, value)
            }
        } catch (any: Exception) {
            logger.error("Failed register alias $value for $name")
        }

        if (ModuleAPI.isModuleExist("cooldown")) {
            CommandAliases.aliases[name] = aliases.toMutableList()
        }

        if (actionNeed) {
            dispatcher.register(literal.executes { process(it) })
            aliases.filter { it != name }.forEach { alias ->
                hack(alias).run { dispatcher.register(literal.executes { process(it) }) }
            }
        } else {
            dispatcher.register(literal)
            aliases.filter { it != name }.forEach { alias ->
                hack(alias).run { dispatcher.register(literal) }
            }
        }
    }

    /**
     * Process command, i.e execute command.
     * @param context command context.
     * @return int. Command execution result.
     * @since 2.0.0-SNAPSHOT.1.
     */
    override fun process(context: CommandContext<CommandSource>): Int {
        logger.debug(
            marker, " :: Executed command ${context.input} by ${context.playerName()}"
        )
        return 0
    }
}
