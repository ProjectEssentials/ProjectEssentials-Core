/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.world.Difficulty
import net.minecraft.world.dimension.DimensionType
import org.apache.logging.log4j.LogManager
import java.util.function.Function

internal object DifficultyCommand {
    private val FAILED_EXCEPTION = DynamicCommandExceptionType(
        Function { p_208823_0_: Any? ->
            TranslationTextComponent(
                "commands.difficulty.failure", p_208823_0_
            )
        }
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.difficulty + "difficulty"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["difficulty"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        NativeCommandUtils.removeCommand("difficulty")
        logger.info("Replacing `/difficulty` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            val literalargumentbuilder =
                Commands.literal(command)

            for (difficulty in Difficulty.values()) {
                literalargumentbuilder.then(
                    Commands.literal(difficulty.translationKey).executes { p_198347_1_ ->
                        setDifficulty(p_198347_1_.source, difficulty)
                    }
                )
            }
            dispatcher.register(
                literalargumentbuilder.executes { p_198346_0_ ->
                    val difficulty1 = p_198346_0_.source.world.difficulty
                    p_198346_0_.source.sendFeedback(
                        TranslationTextComponent(
                            "commands.difficulty.query",
                            difficulty1.displayName
                        ), false
                    )
                    difficulty1.id
                }
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.difficulty", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "difficulty")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.difficulty", "2"
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

    @Throws(CommandSyntaxException::class)
    fun setDifficulty(source: CommandSource, difficulty: Difficulty): Int {
        checkPermissions(source)

        val minecraftserver = source.server
        return if (minecraftserver.getWorld(DimensionType.OVERWORLD).difficulty == difficulty) {
            throw FAILED_EXCEPTION.create(difficulty.translationKey)
        } else {
            minecraftserver.setDifficultyForAllWorlds(difficulty, true)
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.difficulty.success",
                    difficulty.displayName
                ), true
            )
            0
        }
    }
}
