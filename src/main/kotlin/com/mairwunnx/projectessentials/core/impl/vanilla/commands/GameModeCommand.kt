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
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.GameRules
import net.minecraft.world.GameType

internal object GameModeCommand : VanillaCommandBase() {
    private var aliases = configuration.take().aliases.gamemode + "gamemode"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["gamemode"] = aliases.toMutableList()
        CommandAliases.aliases["gamemode"]?.addAll(listOf("gmc", "gms", "gma", "gmsp"))
    }

    private fun registerShortAliases(dispatcher: CommandDispatcher<CommandSource>) {
        Commands.literal("gmc").executes {
            setGameMode(it, setOf(it.source.asPlayer()), GameType.CREATIVE)
        }.then(
            Commands.argument("target", EntityArgument.players()).executes {
                setGameMode(it, EntityArgument.getPlayers(it, "target"), GameType.CREATIVE)
            }
        ).also { dispatcher.register(it) }
        Commands.literal("gms").executes {
            setGameMode(it, setOf(it.source.asPlayer()), GameType.SURVIVAL)
        }.then(
            Commands.argument("target", EntityArgument.players()).executes {
                setGameMode(it, EntityArgument.getPlayers(it, "target"), GameType.SURVIVAL)
            }
        ).also { dispatcher.register(it) }
        Commands.literal("gma").executes {
            setGameMode(it, setOf(it.source.asPlayer()), GameType.ADVENTURE)
        }.then(
            Commands.argument("target", EntityArgument.players()).executes {
                setGameMode(it, EntityArgument.getPlayers(it, "target"), GameType.ADVENTURE)
            }
        ).also { dispatcher.register(it) }
        Commands.literal("gmsp").executes {
            setGameMode(it, setOf(it.source.asPlayer()), GameType.SPECTATOR)
        }.then(
            Commands.argument("target", EntityArgument.players()).executes {
                setGameMode(it, EntityArgument.getPlayers(it, "target"), GameType.SPECTATOR)
            }
        ).also { dispatcher.register(it) }
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("gamemode")
        tryAssignAliases()
        registerShortAliases(dispatcher)

        aliases.forEach { command ->
            val literal = Commands.literal(command)
            GameType.values().forEach { type ->
                if (type != GameType.NOT_SET) {
                    literal.then(
                        Commands.literal(type.getName()).executes {
                            setGameMode(it, setOf(it.source.asPlayer()), type)
                        }.then(
                            Commands.argument(
                                "target", EntityArgument.players()
                            ).executes {
                                setGameMode(it, EntityArgument.getPlayers(it, "target"), type)
                            }
                        )
                    )
                }
            }
            dispatcher.register(literal)
        }
    }

    private fun checkPermissions(source: CommandSource, gameTypeIn: GameType) {
        try {
            if (!hasPermission(source.asPlayer(), "native.gamemode.${gameTypeIn.getName()}", 2)) {
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
                                "native.gamemode.${gameTypeIn.getName()}", "2"
                            )
                        )
                    )
                )
            }
        } catch (_: CommandSyntaxException) {
            // Suppressed, because command executed by server.
        }
    }

    private fun sendGameModeFeedback(
        source: CommandSource,
        player: ServerPlayerEntity,
        gameTypeIn: GameType
    ) {
        val component = TranslationTextComponent("gameMode." + gameTypeIn.getName())
        if (source.entity === player) {
            source.sendFeedback(
                TranslationTextComponent("commands.gamemode.success.self", component), false
            )
        } else {
            if (source.world.gameRules.getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendMessage(TranslationTextComponent("gameMode.changed", component))
            }
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.gamemode.success.other", player.displayName, component
                ), false
            )
        }
    }

    private fun setGameMode(
        source: CommandContext<CommandSource>,
        players: Collection<ServerPlayerEntity>,
        gameTypeIn: GameType
    ): Int {
        checkPermissions(source.source, gameTypeIn)
        var i = 0
        players.forEach {
            if (it.interactionManager.gameType != gameTypeIn) {
                it.setGameType(gameTypeIn)
                sendGameModeFeedback(source.source, it, gameTypeIn)
                ++i
            }
        }
        return i
    }
}

