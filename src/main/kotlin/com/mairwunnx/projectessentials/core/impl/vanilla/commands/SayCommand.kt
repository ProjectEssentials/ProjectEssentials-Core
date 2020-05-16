/**
 * This command implementation by Mojang.
 * And decompiled with idea source code was converted
 * to kotlin code.
 * Also added some logic, for example checking on
 * permissions, and for some commands shorten aliases.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
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
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object SayCommand : VanillaCommandBase() {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("say")

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
            if (!hasPermission(source.asPlayer(), "native.messaging.say", 1)) {
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
                                "native.messaging.say", "1"
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
