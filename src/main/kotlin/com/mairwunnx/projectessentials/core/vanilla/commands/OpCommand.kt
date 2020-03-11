package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
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
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object OpCommand {
    private val ALREADY_OP = SimpleCommandExceptionType(
        TranslationTextComponent("commands.op.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.op + "op"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["op"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/op` vanilla command")
        NativeCommandUtils.removeCommand("op")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "targets", GameProfileArgument.gameProfile()
                    ).suggests { p_198543_0_: CommandContext<CommandSource>, p_198543_1_ ->
                        val playerlist = p_198543_0_.source.server.playerList
                        ISuggestionProvider.suggest(
                            playerlist.players.stream().filter { p_198540_1_ ->
                                !playerlist.canSendCommands(p_198540_1_.gameProfile)
                            }.map { p_200545_0_ ->
                                p_200545_0_.gameProfile.name
                            }, p_198543_1_
                        )
                    }.executes { p_198544_0_ ->
                        opPlayers(
                            p_198544_0_.source,
                            GameProfileArgument.getGameProfiles(p_198544_0_, "targets")
                        )
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.op", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "op")
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
                                "native.op", "3"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun opPlayers(
        source: CommandSource,
        gameProfiles: Collection<GameProfile>
    ): Int {
        checkPermissions(source)

        val playerlist = source.server.playerList
        var i = 0
        for (gameprofile in gameProfiles) {
            if (!playerlist.canSendCommands(gameprofile)) {
                playerlist.addOp(gameprofile)
                ++i
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.op.success",
                        gameProfiles.iterator().next().name
                    ), true
                )
            }
        }
        return if (i == 0) throw ALREADY_OP.create() else i
    }
}
