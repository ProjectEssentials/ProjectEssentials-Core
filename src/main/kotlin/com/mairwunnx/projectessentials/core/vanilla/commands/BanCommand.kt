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
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.GameProfileArgument
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.server.management.ProfileBanEntry
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager
import java.util.*

internal object BanCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.ban + "ban"

    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.ban.failed")
    )

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["ban"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/ban` vanilla command")
        NativeCommandUtils.removeCommand("ban")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).requires { p_198238_0_ ->
                    p_198238_0_.server.playerList.bannedPlayers.isLanServer
                }.then(
                    Commands.argument(
                        "targets",
                        GameProfileArgument.gameProfile()
                    ).executes { p_198234_0_ ->
                        banGameProfiles(
                            p_198234_0_.source,
                            GameProfileArgument.getGameProfiles(p_198234_0_, "targets"),
                            null as ITextComponent?
                        )
                    }.then(
                        Commands.argument(
                            "reason",
                            MessageArgument.message()
                        ).executes { p_198237_0_ ->
                            banGameProfiles(
                                p_198237_0_.source,
                                GameProfileArgument.getGameProfiles(p_198237_0_, "targets"),
                                MessageArgument.getMessage(p_198237_0_, "reason")
                            )
                        }
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.ban", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "ban")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.ban", "3"
                                )
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
    private fun banGameProfiles(
        source: CommandSource,
        gameProfiles: Collection<GameProfile>,
        reason: ITextComponent?
    ): Int {
        checkPermissions(source)

        val banlist = source.server.playerList.bannedPlayers
        var i = 0
        for (gameprofile in gameProfiles) {
            if (!banlist.isBanned(gameprofile)) {
                val profilebanentry = ProfileBanEntry(
                    gameprofile,
                    null as Date?,
                    source.name,
                    null as Date?,
                    reason?.string
                )
                banlist.addEntry(profilebanentry)
                ++i
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.ban.success",
                        TextComponentUtils.getDisplayName(gameprofile),
                        profilebanentry.banReason
                    ), true
                )
                val serverplayerentity =
                    source.server.playerList.getPlayerByUUID(gameprofile.id)
                serverplayerentity?.connection?.disconnect(TranslationTextComponent("multiplayer.disconnect.banned"))
            }
        }
        return if (i == 0) {
            throw FAILED_EXCEPTION.create()
        } else {
            i
        }
    }
}
