/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.google.common.collect.Iterables
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object HelpCommand {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.help.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.help + "help"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["help"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/help` vanilla command")
        tryAssignAliases()

        dispatcher.register(
            Commands.literal("help").executes { p_198511_1_ ->
                val map = dispatcher.getSmartUsage(dispatcher.root, p_198511_1_.source)
                for (s in map.values) {
                    p_198511_1_.source.sendFeedback(StringTextComponent("/$s"), false)
                }
                map.size
            }.then(
                Commands.argument(
                    "command", StringArgumentType.greedyString()
                ).executes { p_198512_1_ ->
                    checkPermissions(p_198512_1_.source)

                    val parseresults = dispatcher.parse(
                        StringArgumentType.getString(p_198512_1_, "command"),
                        p_198512_1_.source
                    )
                    if (parseresults.context.nodes.isEmpty()) {
                        throw FAILED_EXCEPTION.create()
                    } else {
                        val map =
                            dispatcher.getSmartUsage(
                                Iterables.getLast(
                                    parseresults.context.nodes
                                ).node, p_198512_1_.source
                            )
                        for (s in map.values) {
                            p_198512_1_.source.sendFeedback(
                                StringTextComponent("/" + parseresults.reader.string + " " + s),
                                false
                            )
                        }
                        return@executes map.size
                    }
                }
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.help", 0)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "help")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.help.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}

