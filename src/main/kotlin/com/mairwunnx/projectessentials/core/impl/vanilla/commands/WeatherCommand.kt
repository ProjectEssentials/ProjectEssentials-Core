/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object WeatherCommand : VanillaCommandBase() {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("weather")

        dispatcher.register(
            Commands.literal("weather").then(
                Commands.literal("clear").executes { p_198861_0_ ->
                    setClear(p_198861_0_.source, 6000)
                }.then(
                    Commands.argument(
                        "duration", IntegerArgumentType.integer(0, 1000000)
                    ).executes { p_198864_0_ ->
                        setClear(
                            p_198864_0_.source,
                            IntegerArgumentType.getInteger(p_198864_0_, "duration") * 20
                        )
                    }
                )
            ).then(
                Commands.literal("rain").executes { p_198860_0_ ->
                    setRain(p_198860_0_.source, 6000)
                }.then(
                    Commands.argument(
                        "duration", IntegerArgumentType.integer(0, 1000000)
                    ).executes { p_198866_0_ ->
                        setRain(
                            p_198866_0_.source,
                            IntegerArgumentType.getInteger(p_198866_0_, "duration") * 20
                        )
                    }
                )
            ).then(
                Commands.literal("thunder").executes { p_198859_0_ ->
                    setThunder(p_198859_0_.source, 6000)
                }.then(
                    Commands.argument(
                        "duration", IntegerArgumentType.integer(0, 1000000)
                    ).executes { p_198867_0_ ->
                        setThunder(
                            p_198867_0_.source,
                            IntegerArgumentType.getInteger(p_198867_0_, "duration") * 20
                        )
                    }
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.weather", 2)) {
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
                                "native.weather", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun setClear(source: CommandSource, time: Int): Int {
        checkPermissions(source)
        source.world.worldInfo.clearWeatherTime = time
        source.world.worldInfo.rainTime = 0
        source.world.worldInfo.thunderTime = 0
        source.world.worldInfo.isRaining = false
        source.world.worldInfo.isThundering = false
        source.sendFeedback(
            TranslationTextComponent(
                "commands.weather.set.clear"
            ), true
        )
        return time
    }

    private fun setRain(source: CommandSource, time: Int): Int {
        checkPermissions(source)
        source.world.worldInfo.clearWeatherTime = 0
        source.world.worldInfo.rainTime = time
        source.world.worldInfo.thunderTime = time
        source.world.worldInfo.isRaining = true
        source.world.worldInfo.isThundering = false
        source.sendFeedback(
            TranslationTextComponent(
                "commands.weather.set.rain"
            ), true
        )
        return time
    }

    private fun setThunder(source: CommandSource, time: Int): Int {
        checkPermissions(source)
        source.world.worldInfo.clearWeatherTime = 0
        source.world.worldInfo.rainTime = time
        source.world.worldInfo.thunderTime = time
        source.world.worldInfo.isRaining = true
        source.world.worldInfo.isThundering = true
        source.sendFeedback(
            TranslationTextComponent(
                "commands.weather.set.thunder"
            ), true
        )
        return time
    }
}
