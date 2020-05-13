/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

//import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
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
import net.minecraft.util.text.TranslationTextComponent

internal object DeOpCommand : VanillaCommandBase() {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.deop.failed")
    )

    private var aliases = configuration.take().aliases.deop + "deop"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["deop"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("deop")
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
            if (!hasPermission(source.asPlayer(), "native.stuff.operator.remove", 3)) {
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
                                "native.stuff.operator.remove", "3"
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
            // todo: add support to remove `*` permission when we deop player.
            //  if this feature will not implemented until 2.2>.X version, just make
            //  pull request and remove this commented code and this `to-do`. thanks.
//            if (generalConfiguration.getBool(SETTING_DEOP_COMMAND_REMOVE_OP_PERM)) {
//                if (ModuleAPI.isModuleExist("permissions")) {
////                if (PermissionsAPI.hasPermission(it.name, "*")) {
////                    PermissionsAPI.removeUserPermission(it.name, "*")
////                }
//                }
//            }

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

        return if (i == 0) {
            throw FAILED_EXCEPTION.create()
        } else {
            source.server.kickPlayersNotWhitelisted(source)
            i
        }
    }
}

