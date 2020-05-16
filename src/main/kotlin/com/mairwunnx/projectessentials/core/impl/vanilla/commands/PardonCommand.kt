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


import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
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

internal object PardonCommand : VanillaCommandBase() {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.pardon.failed")
    )

    private var aliases =
        configuration.take().aliases.pardon + "pardon"

    private fun tryAssignAliases() {
        CommandAliases.aliases["pardon"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("pardon")
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
            if (!hasPermission(source.asPlayer(), "native.moderation.pardon", 3)) {

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
                                "native.moderation.pardon", "3"
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
