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
import com.mairwunnx.projectessentials.core.api.v1.SETTING_WEATHER_COMMAND_DEFAULT_DURATION
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
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

/**
 * Weather type uses for [WeatherType] enum elements.
 * @since 2.0.0-SNAPSHOT.1.
 */
interface IWeatherType {
    /**
     * Weather type name for permissions checking.
     */
    val type: String
}

/**
 * Weather type enum class, contains all weather types for
 * other essentials modules.
 * @since 2.0.0-SNAPSHOT.1.
 */
enum class WeatherType : IWeatherType {
    /**
     * Sunny in-game weather.
     */
    Sunny {
        override val type = "clear"
    },

    /**
     * Rainy in-game weather.
     */
    Rainy {
        override val type = "rain"
    },

    /**
     * Thunder in-game weather.
     */
    Thunder {
        override val type = "thunder"
    }
}

internal object WeatherCommand : VanillaCommandBase() {
    private var sunAliases = configuration.take().aliases.sun + "sun"
    private var rainAliases = configuration.take().aliases.rain + "rain"
    private var thunderAliases = configuration.take().aliases.thunder + "thunder"

    private val durationArgument = Commands.argument(
        "duration", IntegerArgumentType.integer(0, 1000000)
    )

    private fun tryAssignAliases() {
        CommandAliases.aliases["sun"] = (sunAliases + "weather").toMutableList()
        CommandAliases.aliases["rain"] = (rainAliases + "weather").toMutableList()
        CommandAliases.aliases["thunder"] = (thunderAliases + "weather").toMutableList()
        CommandAliases.aliases["weather"] =
            (sunAliases + rainAliases + thunderAliases).toMutableList()
    }

    private fun registerSun(dispatcher: CommandDispatcher<CommandSource>) {
        sunAliases.forEach {
            dispatcher.register(
                Commands.literal(it).executes { p_198861_0_ ->
                    setClear(
                        p_198861_0_.source,
                        generalConfiguration.getIntOrDefault(
                            SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
                        )
                    )
                }.then(
                    durationArgument.executes { p_198864_0_ ->
                        setClear(
                            p_198864_0_.source,
                            IntegerArgumentType.getInteger(p_198864_0_, "duration") * 20
                        )
                    }
                )
            )
        }
    }

    private fun registerRain(dispatcher: CommandDispatcher<CommandSource>) {
        rainAliases.forEach {
            dispatcher.register(
                Commands.literal(it).executes { p_198860_0_ ->
                    setRain(
                        p_198860_0_.source,
                        generalConfiguration.getIntOrDefault(
                            SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
                        )
                    )
                }.then(
                    durationArgument.executes { p_198866_0_ ->
                        setRain(
                            p_198866_0_.source,
                            IntegerArgumentType.getInteger(p_198866_0_, "duration") * 20
                        )
                    }
                )
            )
        }
    }

    private fun registerThunder(dispatcher: CommandDispatcher<CommandSource>) {
        thunderAliases.forEach {
            dispatcher.register(
                Commands.literal(it).executes { p_198859_0_ ->
                    setThunder(
                        p_198859_0_.source,
                        generalConfiguration.getIntOrDefault(
                            SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
                        )
                    )
                }.then(
                    durationArgument.executes { p_198867_0_ ->
                        setThunder(
                            p_198867_0_.source,
                            IntegerArgumentType.getInteger(p_198867_0_, "duration") * 20
                        )
                    }
                )
            )
        }
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        registerSun(dispatcher)
        registerRain(dispatcher)
        registerThunder(dispatcher)
        tryAssignAliases()
        CommandAPI.removeCommand("weather")

        dispatcher.register(
            Commands.literal("weather").then(
                Commands.literal("clear").executes { p_198861_0_ ->
                    setClear(
                        p_198861_0_.source,
                        generalConfiguration.getIntOrDefault(
                            SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
                        )
                    )
                }.then(
                    durationArgument.executes { p_198864_0_ ->
                        setClear(
                            p_198864_0_.source,
                            IntegerArgumentType.getInteger(p_198864_0_, "duration") * 20
                        )
                    }
                )
            ).then(
                Commands.literal("rain").executes { p_198860_0_ ->
                    setRain(
                        p_198860_0_.source,
                        generalConfiguration.getIntOrDefault(
                            SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
                        )
                    )
                }.then(
                    durationArgument.executes { p_198866_0_ ->
                        setRain(
                            p_198866_0_.source,
                            IntegerArgumentType.getInteger(p_198866_0_, "duration") * 20
                        )
                    }
                )
            ).then(
                Commands.literal("thunder").executes { p_198859_0_ ->
                    setThunder(
                        p_198859_0_.source,
                        generalConfiguration.getIntOrDefault(
                            SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
                        )
                    )
                }.then(
                    durationArgument.executes { p_198867_0_ ->
                        setThunder(
                            p_198867_0_.source,
                            IntegerArgumentType.getInteger(p_198867_0_, "duration") * 20
                        )
                    }
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource, weatherType: WeatherType, timed: Boolean) {
        try {
            if (
                !hasPermission(
                    source.asPlayer(),
                    if (timed) "native.weather.${weatherType.type}.timed" else "native.weather.${weatherType.type}",
                    if (timed) 3 else 2
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
                                if (timed) "native.weather.${weatherType.type}.timed" else "native.weather.${weatherType.type}",
                                if (timed) "3" else "2"
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
        checkPermissions(
            source,
            WeatherType.Sunny,
            time != generalConfiguration.getIntOrDefault(
                SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
            )
        )
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
        checkPermissions(
            source,
            WeatherType.Rainy,
            time != generalConfiguration.getIntOrDefault(
                SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
            )
        )
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
        checkPermissions(
            source,
            WeatherType.Thunder,
            time != generalConfiguration.getIntOrDefault(
                SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000
            )
        )
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
