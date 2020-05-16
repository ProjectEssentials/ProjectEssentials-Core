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
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.command.*
import net.minecraft.command.arguments.FunctionArgument
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object FunctionCommand : VanillaCommandBase() {
    val FUNCTION_SUGGESTER =
        SuggestionProvider { p_198477_0_: CommandContext<CommandSource>, p_198477_1_ ->
            val functionmanager = p_198477_0_.source.server.functionManager
            ISuggestionProvider.suggestIterable(
                functionmanager.tagCollection.registeredTags, p_198477_1_, "#"
            )
            ISuggestionProvider.suggestIterable(functionmanager.functions.keys, p_198477_1_)
        }

    private var aliases =
        configuration.take().aliases.function + "function"

    private fun tryAssignAliases() {
        CommandAliases.aliases["function"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("function")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument("name", FunctionArgument.func_200021_a()).suggests(
                        FUNCTION_SUGGESTER
                    ).executes { p_198479_0_ ->
                        executeFunctions(
                            p_198479_0_.source, FunctionArgument.getFunctions(p_198479_0_, "name")
                        )
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.function", 2)) {
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
                                "native.function", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun executeFunctions(
        source: CommandSource,
        functions: Collection<FunctionObject>
    ): Int {
        checkPermissions(source)

        var i = 0
        for (functionobject in functions) {
            i += source.server.functionManager.execute(
                functionobject,
                source.withFeedbackDisabled().withMinPermissionLevel(2)
            )
        }
        if (functions.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.function.success.single",
                    i, functions.iterator().next().id
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.function.success.multiple",
                    i, functions.size
                ), true
            )
        }
        return i
    }
}

