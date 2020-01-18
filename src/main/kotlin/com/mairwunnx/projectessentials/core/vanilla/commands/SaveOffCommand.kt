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
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object SaveOffCommand {
    private val SAVE_ALREADY_OFF_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.save.alreadyOff")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.saveall + "save-off"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["save-off"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/save-off` vanilla command")
        NativeCommandUtils.removeCommand("save-off")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198618_0_ ->
                    checkPermissions(p_198618_0_.source)
                    val commandsource = p_198618_0_.source
                    var flag = false
                    for (serverworld in commandsource.server.worlds) {
                        if (serverworld != null && !serverworld.disableLevelSaving) {
                            serverworld.disableLevelSaving = true
                            flag = true
                        }
                    }
                    if (!flag) {
                        throw SAVE_ALREADY_OFF_EXCEPTION.create()
                    } else {
                        commandsource.sendFeedback(
                            TranslationTextComponent("commands.save.disabled"),
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
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.saveoff", 4)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "saveoff")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.saveoff.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
