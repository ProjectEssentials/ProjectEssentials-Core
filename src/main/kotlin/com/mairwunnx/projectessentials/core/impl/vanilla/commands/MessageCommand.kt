/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
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

internal object MessageCommand : VanillaCommandBase() {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("msg")
        CommandAPI.removeCommand("tell")
        CommandAPI.removeCommand("w")
        CommandAPI.removeCommand("m")

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
            if (!hasPermission(source.asPlayer(), "native.messaging.message", 0)) {

                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        generalConfiguration.getBool(SETTING_LOC_ENABLED),
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                "native.command.restricted_hover",
                                "native.messaging.message", "0"
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
