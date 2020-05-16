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

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.server.management.BanEntry
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object BanListCommand : VanillaCommandBase() {
    private var aliases =
        configuration.take().aliases.banlist + "banlist"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["banlist"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("banlist")
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
                                playerlist.bannedPlayers.entries, playerlist.bannedIPs.entries
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
            if (!hasPermission(source.asPlayer(), "native.moderation.banlist", 3)) {
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
                                "native.moderation.banlist", "3"
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
                    "commands.banlist.list", bannedPlayerList.size
                ), false
            )
            for (banentry in bannedPlayerList) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.banlist.entry",
                        banentry.displayName, banentry.bannedBy, banentry.banReason
                    ), false
                )
            }
        }
        return bannedPlayerList.size
    }
}

