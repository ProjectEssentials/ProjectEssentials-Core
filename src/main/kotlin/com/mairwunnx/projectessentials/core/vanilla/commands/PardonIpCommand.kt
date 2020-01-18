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
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object PardonIpCommand {
    private val IP_INVALID_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.pardonip.invalid")
    )
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.pardonip.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.pardonip + "pardon-ip"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["pardon-ip"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/pardon-ip` vanilla command")
        NativeCommandUtils.removeCommand("pardon-ip")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).requires { p_198556_0_ ->
                    p_198556_0_.server.playerList.bannedIPs.isLanServer
                }.then(
                    Commands.argument(
                        "target", StringArgumentType.word()
                    ).suggests { p_198554_0_: CommandContext<CommandSource>, p_198554_1_ ->
                        ISuggestionProvider.suggest(
                            p_198554_0_.source.server.playerList.bannedIPs.keys, p_198554_1_
                        )
                    }.executes { p_198555_0_ ->
                        unbanIp(
                            p_198555_0_.source, StringArgumentType.getString(p_198555_0_, "target")
                        )
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.pardonip", 6)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "pardonip")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.pardonip.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun unbanIp(source: CommandSource, ipAddress: String): Int {
        checkPermissions(source)

        val matcher = BanIpCommand.IP_PATTERN.matcher(ipAddress)
        return if (!matcher.matches()) {
            throw IP_INVALID_EXCEPTION.create()
        } else {
            val ipbanlist = source.server.playerList.bannedIPs
            if (!ipbanlist.isBanned(ipAddress)) {
                throw FAILED_EXCEPTION.create()
            } else {
                ipbanlist.removeEntry(ipAddress)
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.pardonip.success",
                        ipAddress
                    ), true
                )
                1
            }
        }
    }
}
