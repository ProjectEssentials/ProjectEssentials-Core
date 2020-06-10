/**
 * ! This command implementation by Mojang Game Studios!
 *
 * Decompiled with idea source code was converted to kotlin code.
 * But with additions such as permissions checking and etc.
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
import com.mairwunnx.projectessentials.core.impl.generalConfiguration
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.impl.GameModeCommand
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.Style
import net.minecraft.world.GameType

internal object GameModeCommand : VanillaCommandBase("gamemode") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher).also { aliases() }
        GameType.values().forEach {
            if (!it.name.startsWith("sp")) short("gm${it.getName()[0]}", it) else short("gmsp", it)
        }
        val literal = Commands.literal(name)
        GameType.values().forEach { type ->
            if (type != GameType.NOT_SET) {
                literal.then(
                    Commands.literal(type.getName()).executes {
                        checkPermissions(it.source, type, false)
                        GameModeCommand.setGameMode(it, setOf(it.source.asPlayer()), type)
                    }.then(
                        Commands.argument("target", EntityArgument.players()).executes {
                            checkPermissions(it.source, type, true)
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
        Commands.literal(short).executes {
            checkPermissions(it.source, mode, false)
            GameModeCommand.setGameMode(it, setOf(it.source.asPlayer()), mode)
        }.then(
            Commands.argument("target", EntityArgument.players()).executes {
                checkPermissions(it.source, mode, true)
                GameModeCommand.setGameMode(it, EntityArgument.getPlayers(it, "target"), mode)
            }
        ).also { CommandAPI.getDispatcher().register(it) }
    }

    private fun checkPermissions(source: CommandSource, gameTypeIn: GameType, other: Boolean) {
        if (source !is ServerPlayerEntity) return
        if (!hasPermission(
                source.asPlayer(),
                "native.gamemode.${gameTypeIn.getName()}${if (other) ".other" else ""}",
                if (other) 3 else 2
            )
        ) {
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
                            "native.gamemode.${gameTypeIn.getName()}${if (other) ".other" else ""}",
                            if (other) "3" else "2"
                        )
                    )
                )
            )
        }
    }
}

