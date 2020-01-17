/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object StopCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/stop` vanilla command")

        dispatcher.register(
            Commands.literal("stop").executes { p_198726_0_ ->
                checkPermissions(p_198726_0_.source)
                p_198726_0_.source.sendFeedback(
                    TranslationTextComponent("commands.stop.stopping"), true
                )
                p_198726_0_.source.server.initiateShutdown(false)
                1
            }
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.stop", 4)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "stop")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.stop.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
