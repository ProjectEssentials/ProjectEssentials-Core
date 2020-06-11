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
import com.mairwunnx.projectessentials.core.impl.nativeMappingsConfiguration
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.impl.WeatherCommand

internal object WeatherCommand : VanillaCommandBase("weather") {
    private val durationArgument = Commands.argument(
        "duration", integer(0, 1000000)
    )

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        short("sun").also { short("rain") }.also { short("thunder") }.also { aliases() }
        dispatcher.register(
            Commands.literal(name).requires {
                isAllowedAny(it) {
                    listOf("weather.sun" to 2, "weather.rain" to 2, "weather.thunder" to 2)
                }
            }.then(
                Commands.literal("clear").requires {
                    isAllowed(it, "weather.sun", 2)
                }.executes {
                    WeatherCommand.setClear(it.source, 6000)
                }.then(
                    durationArgument.executes {
                        WeatherCommand.setClear(it.source, getInteger(it, "duration") * 20)
                    }
                )
            ).then(
                Commands.literal("rain").requires {
                    isAllowed(it, "weather.rain", 2)
                }.executes {
                    WeatherCommand.setRain(it.source, 6000)
                }.then(
                    durationArgument.executes {
                        WeatherCommand.setRain(it.source, getInteger(it, "duration") * 20)
                    }
                )
            ).then(
                Commands.literal("thunder").requires {
                    isAllowed(it, "weather.thunder", 2)
                }.executes {
                    WeatherCommand.setThunder(it.source, 6000)
                }.then(
                    durationArgument.executes {
                        WeatherCommand.setThunder(it.source, getInteger(it, "duration") * 20)
                    }
                )
            )
        )
    }

    private fun short(name: String) {
        aliasesOf(name).forEach { command ->
            CommandAPI.getDispatcher().register(Commands.literal(command).requires {
                isAllowed(it, "weather.$name", 2)
            }.then(
                durationArgument.executes {
                    val duration = getInteger(it, "duration") * 20
                    val source = it.source
                    if (name == "sun") WeatherCommand.setClear(source, duration)
                    if (name == "rain") WeatherCommand.setRain(source, duration)
                    if (name == "thunder") WeatherCommand.setThunder(source, duration)
                    else return@executes -1
                }
            ).executes {
                val source = it.source
                if (name == "sun") WeatherCommand.setClear(source, 6000)
                if (name == "rain") WeatherCommand.setRain(source, 6000)
                if (name == "thunder") WeatherCommand.setThunder(source, 6000)
                else return@executes -1
            })
        }
    }

    private fun aliases() {
        CommandAliases.aliases["sun"] = (aliasesOf("sun") + "weather").toMutableList()
        CommandAliases.aliases["thunder"] = (aliasesOf("thunder") + "weather").toMutableList()
        CommandAliases.aliases["rain"] = (aliasesOf("rain") + "weather").toMutableList()
    }

    private fun aliasesOf(origin: String) = nativeMappingsConfiguration.aliases[origin]?.let {
        return@let it.split(',') + origin
    } ?: let { return@let listOf(origin) }
}
