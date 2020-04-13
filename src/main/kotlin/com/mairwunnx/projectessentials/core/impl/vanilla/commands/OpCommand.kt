package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
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

internal object OpCommand : VanillaCommandBase() {
    private val ALREADY_OP = SimpleCommandExceptionType(
        TranslationTextComponent("commands.op.failed")
    )

    private var aliases = configuration.take().aliases.op + "op"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandsAliases.aliases["op"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("op")
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
            if (!hasPermission(source.asPlayer(), "native.stuff.operator.add", 3)) {
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
                                "native.stuff.operator.add", "3"
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
