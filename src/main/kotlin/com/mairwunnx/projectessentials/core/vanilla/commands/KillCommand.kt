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
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.Entity
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object KillCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.kill + "kill"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["kill"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/kill` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "targets", EntityArgument.entities()
                    ).executes { p_198520_0_ ->
                        killEntities(
                            p_198520_0_.source,
                            EntityArgument.getEntities(p_198520_0_, "targets")
                        )
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.kill", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "kill")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.kill.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun killEntities(
        source: CommandSource, targets: Collection<Entity>
    ): Int {
        checkPermissions(source)

        for (entity in targets) {
            entity.onKillCommand()
        }
        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.kill.success.single",
                    targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.kill.success.multiple",
                    targets.size
                ), true
            )
        }
        return targets.size
    }
}
