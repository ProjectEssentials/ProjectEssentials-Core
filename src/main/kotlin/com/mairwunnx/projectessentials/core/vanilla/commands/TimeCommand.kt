/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.TimeArgument
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.world.server.ServerWorld
import org.apache.logging.log4j.LogManager

internal object TimeCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/time` vanilla command")
        NativeCommandUtils.removeCommand("time")

        dispatcher.register(
            Commands.literal("time").then(
                Commands.literal("set").then(
                    Commands.literal("day").executes { p_198832_0_ ->
                        setTime(p_198832_0_.source, 1000)
                    }
                ).then(
                    Commands.literal("noon").executes { p_198825_0_ ->
                        setTime(p_198825_0_.source, 6000)
                    }
                ).then(
                    Commands.literal("night").executes { p_198822_0_ ->
                        setTime(p_198822_0_.source, 13000)
                    }
                ).then(
                    Commands.literal("midnight").executes { p_200563_0_ ->
                        setTime(p_200563_0_.source, 18000)
                    }
                ).then(
                    Commands.argument(
                        "time", TimeArgument.func_218091_a()
                    ).executes { p_200564_0_ ->
                        setTime(
                            p_200564_0_.source,
                            IntegerArgumentType.getInteger(p_200564_0_, "time")
                        )
                    }
                )
            ).then(
                Commands.literal("add").then(
                    Commands.argument(
                        "time", TimeArgument.func_218091_a()
                    ).executes { p_198830_0_ ->
                        addTime(
                            p_198830_0_.source,
                            IntegerArgumentType.getInteger(p_198830_0_, "time")
                        )
                    }
                )
            ).then(
                Commands.literal("query").then(
                    Commands.literal("daytime").executes { p_198827_0_ ->
                        sendQueryResults(
                            p_198827_0_.source,
                            getDayTime(p_198827_0_.source.world)
                        )
                    }
                ).then(
                    Commands.literal("gametime").executes { p_198821_0_ ->
                        sendQueryResults(
                            p_198821_0_.source,
                            (p_198821_0_.source.world.gameTime % 2147483647L).toInt()
                        )
                    }
                ).then(
                    Commands.literal("day").executes { p_198831_0_ ->
                        sendQueryResults(
                            p_198831_0_.source,
                            (p_198831_0_.source.world.dayTime / 24000L % 2147483647L).toInt()
                        )
                    }
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.time", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "time")
                )
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        LocalizationConfigurationUtils.getConfig().enabled,
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                LocalizationConfigurationUtils.getConfig().enabled,
                                "native.command.restricted_hover",
                                "native.time", "2"
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
    private fun getDayTime(worldIn: ServerWorld): Int {
        return (worldIn.dayTime % 24000L).toInt()
    }

    private fun sendQueryResults(source: CommandSource, time: Int): Int {
        checkPermissions(source)

        source.sendFeedback(
            TranslationTextComponent(
                "commands.time.query", time
            ), false
        )
        return time
    }

    fun setTime(source: CommandSource, time: Int): Int {
        checkPermissions(source)

        for (serverworld in source.server.worlds) {
            serverworld.dayTime = time.toLong()
        }
        source.sendFeedback(
            TranslationTextComponent(
                "commands.time.set", time
            ), true
        )
        return getDayTime(source.world)
    }

    fun addTime(source: CommandSource, amount: Int): Int {
        checkPermissions(source)

        for (serverworld in source.server.worlds) {
            serverworld.dayTime = serverworld.dayTime + amount.toLong()
        }
        val i = getDayTime(source.world)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.time.set", i
            ), true
        )
        return i
    }
}
