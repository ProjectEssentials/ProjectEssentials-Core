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

import com.google.common.collect.Iterables
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object HelpCommand : VanillaCommandBase() {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.help.failed")
    )

    private var aliases =
        configuration.take().aliases.help + "help"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["help"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("help")
        tryAssignAliases()

        dispatcher.register(
            Commands.literal("help").executes { p_198511_1_ ->
                val map = dispatcher.getSmartUsage(dispatcher.root, p_198511_1_.source)
                for (s in map.values) {
                    p_198511_1_.source.sendFeedback(StringTextComponent("/$s"), false)
                }
                map.size
            }.then(
                Commands.argument(
                    "command", StringArgumentType.greedyString()
                ).executes { p_198512_1_ ->
                    checkPermissions(p_198512_1_.source)

                    val parseresults = dispatcher.parse(
                        StringArgumentType.getString(p_198512_1_, "command"),
                        p_198512_1_.source
                    )
                    if (parseresults.context.nodes.isEmpty()) {
                        throw FAILED_EXCEPTION.create()
                    } else {
                        val map =
                            dispatcher.getSmartUsage(
                                Iterables.getLast(parseresults.context.nodes).node,
                                p_198512_1_.source
                            )
                        for (s in map.values) {
                            p_198512_1_.source.sendFeedback(
                                StringTextComponent("/" + parseresults.reader.string + " " + s),
                                false
                            )
                        }
                        return@executes map.size
                    }
                }
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.help", 0)) {
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
                                "native.help", "0"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}

