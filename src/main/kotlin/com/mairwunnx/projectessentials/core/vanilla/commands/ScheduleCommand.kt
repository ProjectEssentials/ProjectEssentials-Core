/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.datafixers.util.Either
import net.minecraft.command.*
import net.minecraft.command.arguments.FunctionArgument
import net.minecraft.command.arguments.TimeArgument
import net.minecraft.tags.Tag
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object ScheduleCommand {
    private val logger = LogManager.getLogger()

    private val field_218913_a = SimpleCommandExceptionType(
        TranslationTextComponent("commands.schedule.same_tick")
    )

    fun register(p_218909_0_: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/schedule` vanilla command")
        NativeCommandUtils.removeCommand("schedule")

        p_218909_0_.register(
            Commands.literal("schedule").then(
                Commands.literal("function").then(
                    Commands.argument(
                        "function", FunctionArgument.func_200021_a()
                    ).suggests(FunctionCommand.FUNCTION_SUGGESTER).then(
                        Commands.argument(
                            "time", TimeArgument.func_218091_a()
                        ).executes { p_218911_0_: CommandContext<CommandSource> ->
                            func_218908_a(
                                p_218911_0_.source,
                                FunctionArgument.func_218110_b(p_218911_0_, "function"),
                                IntegerArgumentType.getInteger(p_218911_0_, "time")
                            )
                        }
                    )
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.schedule", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "schedule")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.schedule", "2"
                                )
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Suppress("FunctionName")
    @Throws(CommandSyntaxException::class)
    private fun func_218908_a(
        p_218908_0_: CommandSource,
        p_218908_1_: Either<FunctionObject, Tag<FunctionObject>>,
        p_218908_2_: Int
    ): Int {
        checkPermissions(p_218908_0_)

        return if (p_218908_2_ == 0) {
            throw field_218913_a.create()
        } else {
            val i = p_218908_0_.world.gameTime + p_218908_2_.toLong()
            p_218908_1_.ifLeft { p_218910_4_ ->
                val resourcelocation = p_218910_4_.id
                p_218908_0_.world.worldInfo.scheduledEvents.scheduleReplaceDuplicate(
                    resourcelocation.toString(),
                    i,
                    TimedFunction(resourcelocation)
                )
                p_218908_0_.sendFeedback(
                    TranslationTextComponent(
                        "commands.schedule.created.function",
                        resourcelocation,
                        p_218908_2_,
                        i
                    ), true
                )
            }.ifRight { p_218907_4_ ->
                val resourcelocation = p_218907_4_.id
                p_218908_0_.world.worldInfo.scheduledEvents
                    .scheduleReplaceDuplicate(
                        "#$resourcelocation",
                        i,
                        TimedFunctionTag(resourcelocation)
                    )
                p_218908_0_.sendFeedback(
                    TranslationTextComponent(
                        "commands.schedule.created.tag",
                        resourcelocation,
                        p_218908_2_,
                        i
                    ), true
                )
            }
            Math.floorMod(i, 2147483647L).toInt()
        }
    }
}
