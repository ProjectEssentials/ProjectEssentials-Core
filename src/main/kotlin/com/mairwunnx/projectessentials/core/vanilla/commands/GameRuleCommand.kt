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
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.world.GameRules.*
import org.apache.logging.log4j.LogManager

@Suppress("FunctionName")
internal object GameRuleCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.gamerule + "gamerule"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["gamerule"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/gamerule` vanilla command")
        NativeCommandUtils.removeCommand("gamerule")
        tryAssignAliases()

        aliases.forEach { command ->
            val literalargumentbuilder = Commands.literal(command)
            func_223590_a(object : IRuleEntryVisitor {
                override fun <T : RuleValue<T>> func_223481_a(
                    p_223481_1_: RuleKey<T>, p_223481_2_: RuleType<T>
                ) {
                    literalargumentbuilder.then(
                        Commands.literal(p_223481_1_.func_223576_a()).executes { p_223483_1_ ->
                            func_223486_b(p_223483_1_.source, p_223481_1_)
                        }.then(
                            p_223481_2_.func_223581_a("value").executes { p_223482_1_ ->
                                func_223485_b(p_223482_1_, p_223481_1_)
                            }
                        )
                    )
                }
            })
            dispatcher.register(literalargumentbuilder)
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.gamerule", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "gamerule")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.gamerule", "2"
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

    private fun <T : RuleValue<T>> func_223485_b(
        p_223485_0_: CommandContext<CommandSource>,
        p_223485_1_: RuleKey<T>
    ): Int {
        checkPermissions(p_223485_0_.source)

        val commandsource = p_223485_0_.source
        val t = commandsource.server.gameRules[p_223485_1_]
        t.func_223554_b(p_223485_0_, "value")
        commandsource.sendFeedback(
            TranslationTextComponent(
                "commands.gamerule.set",
                p_223485_1_.func_223576_a(),
                t.toString()
            ), true
        )
        return t.func_223557_c()
    }

    private fun <T : RuleValue<T>> func_223486_b(
        p_223486_0_: CommandSource,
        p_223486_1_: RuleKey<T>
    ): Int {
        checkPermissions(p_223486_0_)

        val t = p_223486_0_.server.gameRules[p_223486_1_]
        p_223486_0_.sendFeedback(
            TranslationTextComponent(
                "commands.gamerule.query",
                p_223486_1_.func_223576_a(),
                t.toString()
            ), false
        )
        return t.func_223557_c()
    }
}
