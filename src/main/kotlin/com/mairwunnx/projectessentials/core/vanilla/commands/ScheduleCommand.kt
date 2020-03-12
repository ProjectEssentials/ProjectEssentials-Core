/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

@file:Suppress("FunctionName")

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.datafixers.util.Either
import net.minecraft.command.*
import net.minecraft.command.arguments.FunctionArgument
import net.minecraft.command.arguments.TimeArgument
import net.minecraft.command.impl.FunctionCommand
import net.minecraft.tags.Tag
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager
import java.util.function.Function

internal object ScheduleCommand {
    private val logger = LogManager.getLogger()

    private val field_218913_a =
        SimpleCommandExceptionType(TranslationTextComponent("commands.schedule.same_tick"))
    private val field_229811_b_ = DynamicCommandExceptionType(
        Function { p_229818_0_: Any? ->
            TranslationTextComponent("commands.schedule.cleared.failure", p_229818_0_)
        }
    )
    private val field_229812_c_ =
        SuggestionProvider { p_229814_0_: CommandContext<CommandSource>, p_229814_1_: SuggestionsBuilder ->
            ISuggestionProvider.suggest(
                p_229814_0_.source.world.worldInfo.scheduledEvents.func_227574_a_(), p_229814_1_
            )
        }

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
                        ).executes { p_229823_0_ ->
                            func_229816_a_(
                                p_229823_0_.source,
                                FunctionArgument.func_218110_b(p_229823_0_, "function"),
                                IntegerArgumentType.getInteger(p_229823_0_, "time"),
                                true
                            )
                        }.then(
                            Commands.literal("append").executes { p_229822_0_ ->
                                func_229816_a_(
                                    p_229822_0_.source,
                                    FunctionArgument.func_218110_b(p_229822_0_, "function"),
                                    IntegerArgumentType.getInteger(p_229822_0_, "time"),
                                    false
                                )
                            }
                        ).then(
                            Commands.literal("replace").executes { p_229821_0_ ->
                                func_229816_a_(
                                    p_229821_0_.source,
                                    FunctionArgument.func_218110_b(p_229821_0_, "function"),
                                    IntegerArgumentType.getInteger(p_229821_0_, "time"),
                                    true
                                )
                            }
                        )
                    )
                )
            ).then(
                Commands.literal("clear").then(
                    Commands.argument(
                        "function", StringArgumentType.greedyString()
                    ).suggests(field_229812_c_).executes { p_229813_0_ ->
                        func_229817_a_(
                            p_229813_0_.source,
                            StringArgumentType.getString(p_229813_0_, "function")
                        )
                    }
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
                                "native.schedule", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun func_229816_a_(
        p_229816_0_: CommandSource,
        p_229816_1_: Either<FunctionObject, Tag<FunctionObject>>,
        p_229816_2_: Int,
        p_229816_3_: Boolean
    ): Int {
        checkPermissions(p_229816_0_)

        return if (p_229816_2_ == 0) {
            throw field_218913_a.create()
        } else {
            val i = p_229816_0_.world.gameTime + p_229816_2_.toLong()
            val timercallbackmanager =
                p_229816_0_.world.worldInfo.scheduledEvents

            p_229816_1_.ifLeft { p_229820_6_ ->
                val resourcelocation = p_229820_6_.id
                val s = resourcelocation.toString()
                if (p_229816_3_) {
                    timercallbackmanager.func_227575_a_(s)
                }
                timercallbackmanager.func_227576_a_(s, i, TimedFunction(resourcelocation))
                p_229816_0_.sendFeedback(
                    TranslationTextComponent(
                        "commands.schedule.created.function",
                        resourcelocation,
                        p_229816_2_,
                        i
                    ), true
                )
            }.ifRight { p_229819_6_ ->
                val resourcelocation = p_229819_6_.id
                val s = "#$resourcelocation"
                if (p_229816_3_) {
                    timercallbackmanager.func_227575_a_(s)
                }
                timercallbackmanager.func_227576_a_(s, i, TimedFunctionTag(resourcelocation))
                p_229816_0_.sendFeedback(
                    TranslationTextComponent(
                        "commands.schedule.created.tag",
                        resourcelocation,
                        p_229816_2_,
                        i
                    ), true
                )
            }
            Math.floorMod(i, 2147483647L).toInt()
        }
    }


    @Throws(CommandSyntaxException::class)
    private fun func_229817_a_(p_229817_0_: CommandSource, p_229817_1_: String): Int {
        val i = p_229817_0_.world.worldInfo.scheduledEvents.func_227575_a_(p_229817_1_)
        return if (i == 0) {
            throw field_229811_b_.create(p_229817_1_)
        } else {
            p_229817_0_.sendFeedback(
                TranslationTextComponent(
                    "commands.schedule.cleared.success",
                    i,
                    p_229817_1_
                ), true
            )
            i
        }
    }
}
