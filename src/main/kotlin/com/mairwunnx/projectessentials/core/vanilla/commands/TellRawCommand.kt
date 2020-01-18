/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.ComponentArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object TellRawCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.tellraw + "tellraw"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["tellraw"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/tellraw` vanilla command")
        NativeCommandUtils.removeCommand("tellraw")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "targets", EntityArgument.players()
                    ).then(
                        Commands.argument(
                            "message", ComponentArgument.component()
                        ).executes { p_198819_0_ ->
                            checkPermissions(p_198819_0_.source)
                            var i = 0
                            for (serverplayerentity in EntityArgument.getPlayers(
                                p_198819_0_, "targets"
                            )) {
                                serverplayerentity.sendMessage(
                                    TextComponentUtils.updateForEntity(
                                        p_198819_0_.source,
                                        ComponentArgument.getComponent(p_198819_0_, "message"),
                                        serverplayerentity,
                                        0
                                    )
                                )
                                ++i
                            }
                            i
                        }
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.tellraw", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "tellraw")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.tellraw.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
