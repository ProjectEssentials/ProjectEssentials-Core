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
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object PardonCommand {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.pardon.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.pardon + "pardon"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["pardon"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/pardon` vanilla command")
        NativeCommandUtils.removeCommand("pardon")
        tryAssignAliases()

        dispatcher.register(
            Commands.literal("pardon").requires { p_198551_0_ ->
                p_198551_0_.server.playerList.bannedIPs.isLanServer
            }.then(
                Commands.argument(
                    "targets", GameProfileArgument.gameProfile()
                ).suggests { p_198549_0_: CommandContext<CommandSource>, p_198549_1_ ->
                    ISuggestionProvider.suggest(
                        p_198549_0_.source.server.playerList.bannedPlayers.keys, p_198549_1_
                    )
                }.executes { p_198550_0_ ->
                    unbanPlayers(
                        p_198550_0_.source,
                        GameProfileArgument.getGameProfiles(p_198550_0_, "targets")
                    )
                }
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.pardon", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "pardon")
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
                                "native.pardon", "3"
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
    private fun unbanPlayers(
        source: CommandSource,
        gameProfiles: Collection<GameProfile>
    ): Int {
        checkPermissions(source)

        val banlist = source.server.playerList.bannedPlayers
        var i = 0
        for (gameprofile in gameProfiles) {
            if (banlist.isBanned(gameprofile)) {
                banlist.removeEntry(gameprofile)
                ++i
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.pardon.success",
                        TextComponentUtils.getDisplayName(gameprofile)
                    ), true
                )
            }
        }
        return if (i == 0) throw FAILED_EXCEPTION.create() else i
    }
}
