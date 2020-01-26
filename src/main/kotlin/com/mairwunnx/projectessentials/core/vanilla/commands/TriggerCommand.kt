/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.google.common.collect.Lists
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.ObjectiveArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScoreCriteria
import net.minecraft.scoreboard.ScoreObjective
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

internal object TriggerCommand {
    private val logger = LogManager.getLogger()

    private val NOT_PRIMED = SimpleCommandExceptionType(
        TranslationTextComponent("commands.trigger.failed.unprimed")
    )
    private val NOT_A_TRIGGER = SimpleCommandExceptionType(
        TranslationTextComponent("commands.trigger.failed.invalid")
    )

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/trigger` vanilla command")
        NativeCommandUtils.removeCommand("trigger")

        dispatcher.register(
            Commands.literal("trigger").then(
                Commands.argument(
                    "objective", ObjectiveArgument.objective()
                ).suggests { p_198853_0_, p_198853_1_ ->
                    suggestTriggers(p_198853_0_.source, p_198853_1_)
                }.executes { p_198854_0_ ->
                    incrementTrigger(
                        p_198854_0_.source,
                        checkValidTrigger(
                            p_198854_0_.source.asPlayer(),
                            ObjectiveArgument.getObjective(p_198854_0_, "objective")
                        )
                    )
                }.then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "value", IntegerArgumentType.integer()
                        ).executes { p_198849_0_ ->
                            addToTrigger(
                                p_198849_0_.source,
                                checkValidTrigger(
                                    p_198849_0_.source.asPlayer(),
                                    ObjectiveArgument.getObjective(p_198849_0_, "objective")
                                ),
                                IntegerArgumentType.getInteger(p_198849_0_, "value")
                            )
                        }
                    )
                ).then(
                    Commands.literal("set").then(
                        Commands.argument(
                            "value", IntegerArgumentType.integer()
                        ).executes { p_198855_0_ ->
                            setTrigger(
                                p_198855_0_.source,
                                checkValidTrigger(
                                    p_198855_0_.source.asPlayer(),
                                    ObjectiveArgument.getObjective(p_198855_0_, "objective")
                                ),
                                IntegerArgumentType.getInteger(p_198855_0_, "value")
                            )
                        }
                    )
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.trigger", 0)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "trigger")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.trigger", "0"
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

    private fun suggestTriggers(
        source: CommandSource,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        checkPermissions(source)

        val entity = source.entity
        val list: MutableList<String> =
            Lists.newArrayList()
        if (entity != null) {
            val scoreboard: Scoreboard = source.server.scoreboard
            val s = entity.scoreboardName
            for (scoreobjective in scoreboard.scoreObjectives) {
                if (scoreobjective.criteria === ScoreCriteria.TRIGGER && scoreboard.entityHasObjective(
                        s, scoreobjective
                    )
                ) {
                    val score = scoreboard.getOrCreateScore(s, scoreobjective)
                    if (!score.isLocked) {
                        list.add(scoreobjective.name)
                    }
                }
            }
        }
        return ISuggestionProvider.suggest(list, builder)
    }

    private fun addToTrigger(source: CommandSource, objective: Score, amount: Int): Int {
        checkPermissions(source)

        objective.increaseScore(amount)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.trigger.add.success",
                objective.objective!!.func_197890_e(),
                amount
            ), true
        )
        return objective.scorePoints
    }

    private fun setTrigger(source: CommandSource, objective: Score, value: Int): Int {
        checkPermissions(source)

        objective.scorePoints = value
        source.sendFeedback(
            TranslationTextComponent(
                "commands.trigger.set.success",
                objective.objective!!.func_197890_e(),
                value
            ), true
        )
        return value
    }

    private fun incrementTrigger(source: CommandSource, objectives: Score): Int {
        checkPermissions(source)

        objectives.increaseScore(1)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.trigger.simple.success",
                objectives.objective!!.func_197890_e()
            ), true
        )
        return objectives.scorePoints
    }

    @Throws(CommandSyntaxException::class)
    private fun checkValidTrigger(player: ServerPlayerEntity, objective: ScoreObjective): Score {
        return if (objective.criteria !== ScoreCriteria.TRIGGER) {
            throw NOT_A_TRIGGER.create()
        } else {
            val scoreboard = player.worldScoreboard
            val s = player.scoreboardName
            if (!scoreboard.entityHasObjective(s, objective)) {
                throw NOT_PRIMED.create()
            } else {
                val score = scoreboard.getOrCreateScore(s, objective)
                if (score.isLocked) {
                    throw NOT_PRIMED.create()
                } else {
                    score.isLocked = true
                    score
                }
            }
        }
    }
}
