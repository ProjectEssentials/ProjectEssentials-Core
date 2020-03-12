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
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.world.GameType
import org.apache.logging.log4j.LogManager

internal object DefaultGameModeCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.defaultgamemode + "defaultgamemode"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["defaultgamemode"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/defaultgamemode` vanilla command")
        NativeCommandUtils.removeCommand("defaultgamemode")
        tryAssignAliases()

        var literalargumentbuilder: LiteralArgumentBuilder<CommandSource>

        aliases.forEach { command ->
            literalargumentbuilder = Commands.literal(command)

            for (gametype in GameType.values()) {
                if (gametype != GameType.NOT_SET) {
                    literalargumentbuilder.then(
                        Commands.literal(gametype.getName()).executes { p_198343_1_ ->
                            setGameType(p_198343_1_.source, gametype)
                        }
                    )
                }
            }

            dispatcher.register(literalargumentbuilder)
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.defaultgamemode", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "defaultgamemode")
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
                                "native.defaultgamemode", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    /**
     * Set Gametype of player who ran the command
     */
    private fun setGameType(commandSourceIn: CommandSource, gamemode: GameType): Int {
        checkPermissions(commandSourceIn)

        var i = 0
        val minecraftserver = commandSourceIn.server
        minecraftserver.gameType = gamemode
        if (minecraftserver.forceGamemode) {
            for (serverplayerentity in minecraftserver.playerList.players) {
                if (serverplayerentity.interactionManager.gameType != gamemode) {
                    serverplayerentity.setGameType(gamemode)
                    ++i
                }
            }
        }
        commandSourceIn.sendFeedback(
            TranslationTextComponent(
                "commands.defaultgamemode.success",
                gamemode.displayName
            ), true
        )
        return i
    }
}
