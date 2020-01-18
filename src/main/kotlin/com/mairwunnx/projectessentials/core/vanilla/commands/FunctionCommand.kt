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
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.command.*
import net.minecraft.command.arguments.FunctionArgument
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object FunctionCommand {
    val FUNCTION_SUGGESTER =
        SuggestionProvider { p_198477_0_: CommandContext<CommandSource>, p_198477_1_ ->
            val functionmanager = p_198477_0_.source.server.functionManager
            ISuggestionProvider.suggestIterable(
                functionmanager.tagCollection.registeredTags, p_198477_1_, "#"
            )
            ISuggestionProvider.suggestIterable(functionmanager.functions.keys, p_198477_1_)
        }

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.function + "function"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["function"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/function` vanilla command")
        NativeCommandUtils.removeCommand("function")
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
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.function", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "function")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.function.restricted"
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
                    i,
                    functions.iterator().next().id
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.function.success.multiple",
                    i,
                    functions.size
                ), true
            )
        }
        return i
    }
}

