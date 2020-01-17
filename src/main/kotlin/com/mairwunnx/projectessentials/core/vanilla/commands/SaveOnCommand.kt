/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

object SaveOnCommand {
    private val SAVE_ALREADY_ON_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.save.alreadyOn")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.saveon + "save-on"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["save-on"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/save-on` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198622_0_ ->
                    checkPermissions(p_198622_0_.source)
                    val commandsource = p_198622_0_.source
                    var flag = false
                    for (serverworld in commandsource.server.worlds) {
                        if (serverworld != null && serverworld.disableLevelSaving) {
                            serverworld.disableLevelSaving = false
                            flag = true
                        }
                    }
                    if (!flag) {
                        throw SAVE_ALREADY_ON_EXCEPTION.create()
                    } else {
                        commandsource.sendFeedback(
                            TranslationTextComponent("commands.save.enabled"),
                            true
                        )
                        return@executes 1
                    }
                }
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.saveon", 4)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "saveon")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.saveon.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
