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
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.TimeArgument
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld

enum class TimeActionType { Add, Set, Query }

internal object TimeCommand : VanillaCommandBase() {
    private var noonAliases = configuration.take().aliases.noon + "noon"
    private var sunsetAliases = configuration.take().aliases.sunset + "sunset"
    private var sunriseAliases = configuration.take().aliases.sunrise + "sunrise"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["noon"] = (noonAliases + "time").toMutableList()
        CommandAliases.aliases["sunset"] = (sunsetAliases + "time").toMutableList()
        CommandAliases.aliases["sunrise"] = (sunriseAliases + "time").toMutableList()
        CommandAliases.aliases["time"] =
            (noonAliases + sunsetAliases + sunriseAliases + "day" + "night" + "midnight").toMutableList()
    }

    fun registerShortAliases(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(Commands.literal("day").executes { setTime(it.source, 1000) })
        noonAliases.forEach { alias ->
            dispatcher.register(Commands.literal(alias).executes { setTime(it.source, 6000) })
        }
        sunsetAliases.forEach { alias ->
            dispatcher.register(Commands.literal(alias).executes { setTime(it.source, 12000) })
        }
        dispatcher.register(Commands.literal("night").executes { setTime(it.source, 13000) })
        dispatcher.register(Commands.literal("midnight").executes { setTime(it.source, 18000) })
        sunriseAliases.forEach { alias ->
            dispatcher.register(Commands.literal(alias).executes { setTime(it.source, 23000) })
        }
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        registerShortAliases(dispatcher)
        CommandAPI.removeCommand("time")
        tryAssignAliases()

        dispatcher.register(
            Commands.literal("time").then(
                Commands.literal("set").then(
                    Commands.literal("day").executes { setTime(it.source, 1000) }
                ).then(
                    Commands.literal("noon").executes { setTime(it.source, 6000) }
                ).then(
                    Commands.literal("sunset").executes { setTime(it.source, 12000) }
                ).then(
                    Commands.literal("night").executes { setTime(it.source, 13000) }
                ).then(
                    Commands.literal("midnight").executes { setTime(it.source, 18000) }
                ).then(
                    Commands.literal("sunrise").executes { setTime(it.source, 23000) }
                ).then(
                    Commands.argument(
                        "time", TimeArgument.func_218091_a()
                    ).executes {
                        setTime(it.source, IntegerArgumentType.getInteger(it, "time"))
                    }
                )
            ).then(
                Commands.literal("add").then(
                    Commands.argument(
                        "time", TimeArgument.func_218091_a()
                    ).executes { addTime(it.source, IntegerArgumentType.getInteger(it, "time")) }
                )
            ).then(
                Commands.literal("query").then(
                    Commands.literal("daytime").executes {
                        sendQueryResults(it.source, getDayTime(it.source.world))
                    }
                ).then(
                    Commands.literal("gametime").executes {
                        sendQueryResults(
                            it.source, (it.source.world.gameTime % 2147483647L).toInt()
                        )
                    }
                ).then(
                    Commands.literal("day").executes {
                        sendQueryResults(
                            it.source, (it.source.world.dayTime / 24000L % 2147483647L).toInt()
                        )
                    }
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource, actionType: TimeActionType) {
        try {
            val node = if (actionType == TimeActionType.Add || actionType == TimeActionType.Set) {
                "native.time.change.${actionType.name.toLowerCase()}"
            } else {
                "native.time.query"
            }

            if (!hasPermission(source.asPlayer(), node, 2)) {
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
                                node, "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    /**
     * Returns the day time (time wrapped within a day)
     */
    private fun getDayTime(worldIn: ServerWorld) = (worldIn.dayTime % 24000L).toInt()

    private fun sendQueryResults(source: CommandSource, time: Int): Int {
        checkPermissions(source, TimeActionType.Query)
        source.sendFeedback(
            TranslationTextComponent("commands.time.query", time), false
        )
        return time
    }

    fun setTime(source: CommandSource, time: Int): Int {
        checkPermissions(source, TimeActionType.Set)
        source.server.worlds.forEach { it.dayTime = time.toLong() }
        source.sendFeedback(
            TranslationTextComponent("commands.time.set", time), true
        )
        return getDayTime(source.world)
    }

    fun addTime(source: CommandSource, amount: Int): Int {
        checkPermissions(source, TimeActionType.Add)
        source.server.worlds.forEach { it.dayTime = it.dayTime + amount.toLong() }
        val i = getDayTime(source.world)
        source.sendFeedback(
            TranslationTextComponent("commands.time.set", i), true
        )
        return i
    }
}
