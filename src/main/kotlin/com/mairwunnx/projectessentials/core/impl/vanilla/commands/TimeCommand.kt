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

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI.getDispatcher
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.impl.nativeMappingsConfiguration
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.TimeArgument
import net.minecraft.command.impl.TimeCommand

internal object TimeCommand : VanillaCommandBase("time") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        short("day", 1000)
        short("noon", 6000)
        short("sunset", 12000)
        short("night", 13000)
        short("midnight", 18000)
        short("sunrise", 23000)
        aliases()

        dispatcher.register(
            literal("time").then(
                literal("set").then(
                    literal("day").requires { permission(it, "set") }.executes {
                        TimeCommand.setTime(it.source, 1000)
                    }
                ).then(
                    literal("noon").requires { permission(it, "set") }.executes {
                        TimeCommand.setTime(it.source, 6000)
                    }
                ).then(
                    literal("sunset").requires { permission(it, "set") }.executes {
                        TimeCommand.setTime(it.source, 12000)
                    }
                ).then(
                    literal("night").requires { permission(it, "set") }.executes {
                        TimeCommand.setTime(it.source, 13000)
                    }
                ).then(
                    literal("midnight").requires { permission(it, "set") }.executes {
                        TimeCommand.setTime(it.source, 18000)
                    }
                ).then(
                    literal("sunrise").requires { permission(it, "set") }.executes {
                        TimeCommand.setTime(it.source, 23000)
                    }
                ).then(
                    Commands.argument(
                        "time", TimeArgument.func_218091_a()
                    ).requires { permission(it, "set") }.executes {
                        TimeCommand.setTime(it.source, IntegerArgumentType.getInteger(it, "time"))
                    }
                )
            ).then(
                literal("add").then(
                    Commands.argument(
                        "time", TimeArgument.func_218091_a()
                    ).requires { permission(it, "add") }.executes {
                        TimeCommand.addTime(it.source, IntegerArgumentType.getInteger(it, "time"))
                    }
                )
            ).then(
                literal("query").then(
                    literal("daytime").requires {
                        permission(it, "query")
                    }.executes {
                        TimeCommand.sendQueryResults(
                            it.source, TimeCommand.getDayTime(it.source.world)
                        )
                    }
                ).then(
                    literal("gametime").requires {
                        permission(it, "query")
                    }.executes {
                        TimeCommand.sendQueryResults(
                            it.source, (it.source.world.gameTime % 2147483647L).toInt()
                        )
                    }
                ).then(
                    literal("day").requires {
                        permission(it, "query")
                    }.executes {
                        TimeCommand.sendQueryResults(
                            it.source, (it.source.world.dayTime / 24000L % 2147483647L).toInt()
                        )
                    }
                )
            )
        )
    }

    private fun permission(source: CommandSource, type: String): Boolean {
        val node = if (type == "add" || type == "set") {
            "time.change.$type"
        } else "time.query"
        return isAllowed(source, node, 2)
    }

    fun short(name: String, time: Int) {
        aliasesOf(name).forEach { command ->
            getDispatcher().register(literal(command).requires {
                permission(it, "set")
            }.executes { TimeCommand.setTime(it.source, time) })
        }
    }

    private fun aliases() {
        CommandAliases.aliases["noon"] = (aliasesOf("noon") + "time").toMutableList()
        CommandAliases.aliases["sunset"] = (aliasesOf("sunset") + "time").toMutableList()
        CommandAliases.aliases["sunrise"] = (aliasesOf("sunrise") + "time").toMutableList()
        CommandAliases.aliases["night"] = (aliasesOf("night") + "time").toMutableList()
        CommandAliases.aliases["midnight"] = (aliasesOf("midnight") + "time").toMutableList()
    }

    private fun aliasesOf(origin: String) = nativeMappingsConfiguration.aliases[origin]?.let {
        return@let it.split(',') + origin
    } ?: let { return@let listOf(origin) }
}
