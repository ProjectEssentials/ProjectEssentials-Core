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
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.GameProfileArgument
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

object DeOpCommand {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.deop.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases = CommandsConfigurationUtils.getConfig().aliases.deop + "deop"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["deop"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/deop` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "targets", GameProfileArgument.gameProfile()
                    ).suggests { p_198323_0_: CommandContext<CommandSource>, p_198323_1_ ->
                        ISuggestionProvider.suggest(
                            p_198323_0_.source.server.playerList.oppedPlayerNames, p_198323_1_
                        )
                    }.executes { p_198324_0_ ->
                        deopPlayers(
                            p_198324_0_.source,
                            GameProfileArgument.getGameProfiles(p_198324_0_, "targets")
                        )
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.deop", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "deop")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.deop.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun deopPlayers(
        source: CommandSource,
        players: Collection<GameProfile>
    ): Int {
        checkPermissions(source)

        val playerlist = source.server.playerList
        var i = 0
        for (gameprofile in players) {
            if (playerlist.canSendCommands(gameprofile)) {
                playerlist.removeOp(gameprofile)
                if (i == 0) {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.deop.success",
                            players.iterator().next().name
                        ), true
                    )
                }
                ++i
            }
        }

        players.forEach {
            if (PermissionsAPI.hasPermission(it.name, "*")) {
                PermissionsAPI.removeUserPermission(it.name, "*")
                if (i == 0) {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.deop.success",
                            players.iterator().next().name
                        ), true
                    )
                }
                ++i
            }
        }

        return if (i == 0) {
            throw FAILED_EXCEPTION.create()
        } else {
            source.server.kickPlayersNotWhitelisted(source)
            i
        }
    }
}

