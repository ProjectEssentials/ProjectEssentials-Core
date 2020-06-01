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
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.GameRules.*

@Suppress("FunctionName")
internal object GameRuleCommand : VanillaCommandBase() {
    private var aliases =
        configuration.take().aliases.gamerule + "gamerule"

    private fun tryAssignAliases() {
        CommandAliases.aliases["gamerule"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("gamerule")
        tryAssignAliases()

        aliases.forEach { command ->
            val literalargumentbuilder = Commands.literal(command)
            visitAll(object : IRuleEntryVisitor {
                override fun <T : RuleValue<T>?> visit(
                    key: RuleKey<T>,
                    type: RuleType<T>
                ) {
                    literalargumentbuilder.then(
                        Commands.literal(key.name)
                            .executes { p_223483_1_: CommandContext<CommandSource> ->
                                func_223486_b(p_223483_1_.source, key)
                            }.then(
                                type.createArgument("value")
                                    .executes { p_223482_1_: CommandContext<CommandSource> ->
                                        func_223485_b(p_223482_1_, key)
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
            if (!hasPermission(source.asPlayer(), "native.gamerule", 2)) {
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
                                "native.gamerule", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun <T : RuleValue<T>?> func_223485_b(
        p_223485_0_: CommandContext<CommandSource>,
        p_223485_1_: RuleKey<T>
    ): Int {
        val commandsource = p_223485_0_.source
        val t = commandsource.server.gameRules[p_223485_1_]
        t!!.updateValue(p_223485_0_, "value")
        commandsource.sendFeedback(
            TranslationTextComponent(
                "commands.gamerule.set",
                p_223485_1_.name,
                t.toString()
            ), true
        )
        return t.intValue()
    }

    private fun <T : RuleValue<T>?> func_223486_b(
        p_223486_0_: CommandSource,
        p_223486_1_: RuleKey<T>
    ): Int {
        val t = p_223486_0_.server.gameRules[p_223486_1_]
        p_223486_0_.sendFeedback(
            TranslationTextComponent(
                "commands.gamerule.query",
                p_223486_1_.name,
                t.toString()
            ), false
        )
        return t!!.intValue()
    }
}
