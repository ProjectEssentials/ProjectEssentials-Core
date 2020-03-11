/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
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
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object KickCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.kick + "kick"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["kick"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/kick` vanilla command")
        NativeCommandUtils.removeCommand("kick")
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
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.kick", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "kick")
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
                                "native.kick", "3"
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
