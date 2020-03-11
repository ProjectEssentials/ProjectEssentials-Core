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
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.GameRules
import net.minecraft.world.GameType
import org.apache.logging.log4j.LogManager

internal object GameModeCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.gamemode + "gamemode"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["gamemode"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/gamemode` vanilla command")
        NativeCommandUtils.removeCommand("gamemode")
        tryAssignAliases()

        aliases.forEach { command ->
            val literalargumentbuilder = Commands.literal(command)
            for (gametype in GameType.values()) {
                if (gametype != GameType.NOT_SET) {
                    literalargumentbuilder.then(
                        Commands.literal(gametype.getName()).executes { p_198483_1_ ->
                            setGameMode(p_198483_1_, setOf(p_198483_1_.source.asPlayer()), gametype)
                        }.then(
                            Commands.argument(
                                "target", EntityArgument.players()
                            ).executes { p_198486_1_ ->
                                setGameMode(
                                    p_198486_1_,
                                    EntityArgument.getPlayers(p_198486_1_, "target"),
                                    gametype
                                )
                            }
                        )
                    )
                }
            }
            dispatcher.register(literalargumentbuilder)
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.gamemode", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "gamemode")
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
                                "native.gamemode", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun sendGameModeFeedback(
        source: CommandSource,
        player: ServerPlayerEntity,
        gameTypeIn: GameType
    ) {
        val itextcomponent: ITextComponent = TranslationTextComponent(
            "gameMode." + gameTypeIn.getName()
        )
        if (source.entity === player) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.gamemode.success.self", itextcomponent
                ), true
            )
        } else {
            if (source.world.gameRules.getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendMessage(
                    TranslationTextComponent(
                        "gameMode.changed", itextcomponent
                    )
                )
            }
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.gamemode.success.other",
                    player.displayName,
                    itextcomponent
                ), true
            )
        }
    }

    private fun setGameMode(
        source: CommandContext<CommandSource>,
        players: Collection<ServerPlayerEntity>,
        gameTypeIn: GameType
    ): Int {
        checkPermissions(source.source)

        var i = 0
        for (serverplayerentity in players) {
            if (serverplayerentity.interactionManager.gameType != gameTypeIn) {
                serverplayerentity.setGameType(gameTypeIn)
                sendGameModeFeedback(source.source, serverplayerentity, gameTypeIn)
                ++i
            }
        }
        return i
    }
}

