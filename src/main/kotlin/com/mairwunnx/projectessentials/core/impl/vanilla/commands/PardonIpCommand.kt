/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object PardonIpCommand : VanillaCommandBase() {
    private val IP_INVALID_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.pardonip.invalid")
    )
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.pardonip.failed")
    )

    private var aliases =
        configuration.take().aliases.pardonip + "pardon-ip"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandsAliases.aliases["pardon-ip"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("pardon-ip")
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
            if (!hasPermission(source.asPlayer(), "native.moderation.pardonip", 3)) {
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
                                "native.moderation.pardonip", "3"
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
