/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object MessageCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/msg` vanilla command")
        NativeCommandUtils.removeCommand("msg")
        NativeCommandUtils.removeCommand("tell")
        NativeCommandUtils.removeCommand("w")
        NativeCommandUtils.removeCommand("m")

        val literalcommandnode = dispatcher.register(
            Commands.literal("msg").then(
                Commands.argument(
                    "targets", EntityArgument.players()
                ).then(
                    Commands.argument(
                        "message", MessageArgument.message()
                    ).executes { p_198539_0_ ->
                        sendPrivateMessage(
                            p_198539_0_.source,
                            EntityArgument.getPlayers(p_198539_0_, "targets"),
                            MessageArgument.getMessage(p_198539_0_, "message")
                        )
                    }
                )
            )
        )
        dispatcher.register(Commands.literal("tell").redirect(literalcommandnode))
        dispatcher.register(Commands.literal("m").redirect(literalcommandnode))
        dispatcher.register(Commands.literal("w").redirect(literalcommandnode))
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.message", 0)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "message")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.message", "0"
                                )
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun sendPrivateMessage(
        source: CommandSource,
        recipients: Collection<ServerPlayerEntity>,
        message: ITextComponent
    ): Int {
        checkPermissions(source)

        for (serverplayerentity in recipients) {
            serverplayerentity.sendMessage(
                TranslationTextComponent(
                    "commands.message.display.incoming",
                    source.displayName,
                    message.deepCopy()
                ).applyTextStyles(
                    TextFormatting.GRAY, TextFormatting.ITALIC
                )
            )
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.message.display.outgoing",
                    serverplayerentity.displayName,
                    message.deepCopy()
                ).applyTextStyles(
                    TextFormatting.GRAY, TextFormatting.ITALIC
                ), false
            )
        }
        return recipients.size
    }
}
