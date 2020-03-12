/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object SayCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/say` vanilla command")
        NativeCommandUtils.removeCommand("say")

        dispatcher.register(
            Commands.literal("say").then(
                Commands.argument(
                    "message", MessageArgument.message()
                ).executes { p_198626_0_ ->
                    checkPermissions(p_198626_0_.source)
                    val itextcomponent = MessageArgument.getMessage(
                        p_198626_0_, "message"
                    )
                    p_198626_0_.source.server.playerList.sendMessage(
                        TranslationTextComponent(
                            "chat.type.announcement",
                            p_198626_0_.source.displayName,
                            itextcomponent
                        )
                    )
                    1
                }
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.say", 1)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "say")
                )
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        LocalizationConfigurationUtils.getConfig().enabled,
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                LocalizationConfigurationUtils.getConfig().enabled,
                                "native.command.restricted_hover",
                                "native.say", "1"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
