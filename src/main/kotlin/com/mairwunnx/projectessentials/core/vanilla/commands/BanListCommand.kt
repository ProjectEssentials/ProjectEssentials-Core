/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
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
import net.minecraft.server.management.BanEntry
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object BanListCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.banlist + "banlist"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["banlist"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/banlist` vanilla command")
        NativeCommandUtils.removeCommand("banlist")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).requires { p_198233_0_ ->
                    (p_198233_0_.server.playerList.bannedPlayers.isLanServer ||
                            p_198233_0_.server.playerList.bannedIPs.isLanServer)
                }.executes { p_198231_0_ ->
                    val playerlist = p_198231_0_.source.server.playerList
                    sendBanList(
                        p_198231_0_.source, Lists.newArrayList(
                            Iterables.concat(
                                playerlist.bannedPlayers.entries,
                                playerlist.bannedIPs.entries
                            )
                        )
                    )
                }.then(
                    Commands.literal("ips").executes { p_198228_0_ ->
                        sendBanList(
                            p_198228_0_.source,
                            p_198228_0_.source.server.playerList.bannedIPs.entries
                        )
                    }).then(
                    Commands.literal("players").executes { p_198232_0_ ->
                        sendBanList(
                            p_198232_0_.source,
                            p_198232_0_.source.server.playerList.bannedPlayers.entries
                        )
                    }
                )
            )

        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.banlist", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "banlist")
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
                                "native.banlist", "3"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun sendBanList(
        source: CommandSource,
        bannedPlayerList: Collection<BanEntry<*>>
    ): Int {
        checkPermissions(source)

        if (bannedPlayerList.isEmpty()) {
            source.sendFeedback(
                TranslationTextComponent("commands.banlist.none"), false
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.banlist.list",
                    bannedPlayerList.size
                ), false
            )
            for (banentry in bannedPlayerList) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.banlist.entry",
                        banentry.displayName,
                        banentry.bannedBy,
                        banentry.banReason
                    ), false
                )
            }
        }
        return bannedPlayerList.size
    }
}

