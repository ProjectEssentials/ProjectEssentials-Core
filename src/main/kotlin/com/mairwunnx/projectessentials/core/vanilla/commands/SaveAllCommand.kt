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
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

object SaveAllCommand {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.save.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.saveall + "save-all"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["save-all"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/save-all` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198610_0_ ->
                    saveAll(p_198610_0_.source, false)
                }.then(
                    Commands.literal("flush").executes { p_198613_0_ ->
                        saveAll(p_198613_0_.source, true)
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.saveall", 4)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "saveall")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.saveall.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun saveAll(source: CommandSource, flush: Boolean): Int {
        checkPermissions(source)
        source.sendFeedback(TranslationTextComponent("commands.save.saving"), false)
        val minecraftserver = source.server
        minecraftserver.playerList.saveAllPlayerData()
        val flag = minecraftserver.save(true, flush, true)
        return if (!flag) {
            throw FAILED_EXCEPTION.create()
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.save.success"
                ), true
            )
            1
        }
    }
}
