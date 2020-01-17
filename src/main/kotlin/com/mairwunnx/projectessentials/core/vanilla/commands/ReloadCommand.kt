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

object ReloadCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/reload` vanilla command")

        dispatcher.register(
            Commands.literal("reload").executes {
                checkPermissions(it.source)
                it.source.sendFeedback(
                    TranslationTextComponent("commands.reload.success"), true
                )
                it.source.server.reload()
                0
            }
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.reload", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "reload")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.reload.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
