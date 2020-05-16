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
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
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
import net.minecraft.util.text.TranslationTextComponent

internal object KickCommand : VanillaCommandBase() {
    private var aliases =
        configuration.take().aliases.kick + "kick"

    private fun tryAssignAliases() {
        CommandAliases.aliases["kick"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("kick")
        tryAssignAliases()

        dispatcher.register(
            Commands.literal("kick").then(
                Commands.argument(
                    "targets", EntityArgument.players()
                ).executes { p_198513_0_ ->
                    kickPlayers(
                        p_198513_0_.source,
                        EntityArgument.getPlayers(p_198513_0_, "targets"),
                        TranslationTextComponent("multiplayer.disconnect.kicked")
                    )
                }.then(
                    Commands.argument(
                        "reason", MessageArgument.message()
                    ).executes { p_198516_0_ ->
                        kickPlayers(
                            p_198516_0_.source,
                            EntityArgument.getPlayers(p_198516_0_, "targets"),
                            MessageArgument.getMessage(p_198516_0_, "reason")
                        )
                    }
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.moderation.kick", 3)) {
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
                                "native.moderation.kick", "3"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun kickPlayers(
        source: CommandSource,
        players: Collection<ServerPlayerEntity>,
        reason: ITextComponent
    ): Int {
        checkPermissions(source)

        for (serverplayerentity in players) {
            serverplayerentity.connection.disconnect(reason)
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.kick.success",
                    serverplayerentity.displayName,
                    reason
                ), true
            )
        }
        return players.size
    }
}
