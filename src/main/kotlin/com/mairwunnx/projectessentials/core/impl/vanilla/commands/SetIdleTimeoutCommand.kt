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
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object SetIdleTimeoutCommand : VanillaCommandBase() {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("setidletimeout")

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
            if (!hasPermission(source.asPlayer(), "native.setidletimeout", 3)) {
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
                                "native.setidletimeout", "3"
                            )
                        )
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
