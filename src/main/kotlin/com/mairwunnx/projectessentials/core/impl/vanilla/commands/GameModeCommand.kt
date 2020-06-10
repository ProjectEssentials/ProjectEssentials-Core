/**
 * ! This command implementation by Mojang Studios!
 *
 * Decompiled with idea source code was converted to kotlin code.
 * But with additions such as permissions checking and etc.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */
package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.impl.GameModeCommand
import net.minecraft.world.GameType

internal object GameModeCommand : VanillaCommandBase("gamemode") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher).also { aliases() }
        // @formatter:off
        GameType.values().forEach {
            if (it != GameType.NOT_SET) {
                if (!it.getName().startsWith("sp")) short("gm${it.getName()[0]}", it) else short("gmsp", it)
            }
        }
        // @formatter:on
        val literal = Commands.literal(name)
        GameType.values().forEach { type ->
            if (type != GameType.NOT_SET) {
                literal.then(
                    Commands.literal(type.getName()).requires {
                        isAllowed(it, "gamemode.${type.getName()}", 2)
                    }.executes {
                        GameModeCommand.setGameMode(it, setOf(it.source.asPlayer()), type)
                    }.then(
                        Commands.argument("target", EntityArgument.players()).requires {
                            isAllowed(it, "gamemode.${type.getName()}.other", 3)
                        }.executes {
                            GameModeCommand.setGameMode(
                                it, EntityArgument.getPlayers(it, "target"), type
                            )
                        }
                    )
                )
            }
        }.also { dispatcher.register(literal) }
    }

    private fun aliases() {
        CommandAliases.aliases["gamemode"]?.addAll(listOf("gmc", "gms", "gma", "gmsp")) ?: run {
            CommandAliases.aliases.put("gamemode", mutableListOf("gmc", "gms", "gma", "gmsp"))
        }
    }

    private fun short(short: String, mode: GameType) {
        Commands.literal(short).requires {
            isAllowed(it, "gamemode.${mode.getName()}", 2)
        }.executes {
            GameModeCommand.setGameMode(it, setOf(it.source.asPlayer()), mode)
        }.then(
            Commands.argument("target", EntityArgument.players()).requires {
                isAllowed(it, "gamemode.${mode.getName()}.other", 3)
            }.executes {
                GameModeCommand.setGameMode(it, EntityArgument.getPlayers(it, "target"), mode)
            }
        ).also { CommandAPI.getDispatcher().register(it) }
    }
}
