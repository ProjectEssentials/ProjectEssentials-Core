/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

object SetIdleTimeoutCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/setidletimeout` vanilla command")

        dispatcher.register(
            Commands.literal("setidletimeout").then(
                Commands.argument(
                    "minutes", IntegerArgumentType.integer(0)
                ).executes { p_198691_0_ ->
                    checkPermissions(p_198691_0_.source)
                    setTimeout(
                        p_198691_0_.source,
                        IntegerArgumentType.getInteger(p_198691_0_, "minutes")
                    )
                }
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.setidletimeout", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "setidletimeout")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.setidletimeout.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun setTimeout(source: CommandSource, idleTimeout: Int): Int {
        source.server.setPlayerIdleTimeout(idleTimeout)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.setidletimeout.success",
                idleTimeout
            ), true
        )
        return idleTimeout
    }
}
