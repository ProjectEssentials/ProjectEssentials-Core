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
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntitySelector
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.server.management.IPBanEntry
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.regex.Pattern

internal object BanIpCommand {
    val IP_PATTERN: Pattern =
        Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")
    private val IP_INVALID = SimpleCommandExceptionType(
        TranslationTextComponent("commands.banip.invalid")
    )
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.banip.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.banip + "ban-ip"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["banip"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/ban-ip` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).requires { p_198222_0_ ->
                    p_198222_0_.server.playerList.bannedIPs.isLanServer
                }.then(
                    Commands.argument(
                        "target",
                        StringArgumentType.word()
                    ).executes { p_198219_0_ ->
                        banUsernameOrIp(
                            p_198219_0_.source,
                            StringArgumentType.getString(p_198219_0_, "target"),
                            null as ITextComponent?
                        )
                    }.then(
                        Commands.argument(
                            "reason",
                            MessageArgument.message()
                        ).executes { p_198221_0_ ->
                            banUsernameOrIp(
                                p_198221_0_.source,
                                StringArgumentType.getString(p_198221_0_, "target"),
                                MessageArgument.getMessage(p_198221_0_, "reason")
                            )
                        }
                    )
                )
            )
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun banUsernameOrIp(
        source: CommandSource,
        username: String,
        reason: ITextComponent?
    ): Int {
        checkPermissions(source)

        val matcher = IP_PATTERN.matcher(username)
        return if (matcher.matches()) {
            banIpAddress(source, username, reason)
        } else {
            val serverplayerentity =
                source.server.playerList.getPlayerByUsername(username)
            if (serverplayerentity != null) {
                banIpAddress(source, serverplayerentity.playerIP, reason)
            } else {
                throw IP_INVALID.create()
            }
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.banip", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "banip")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.banip.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun banIpAddress(
        source: CommandSource,
        ip: String,
        reason: ITextComponent?
    ): Int {
        val ipbanlist = source.server.playerList.bannedIPs
        return if (ipbanlist.isBanned(ip)) {
            throw FAILED_EXCEPTION.create()
        } else {
            val list =
                source.server.playerList.getPlayersMatchingAddress(ip)
            val ipbanentry = IPBanEntry(
                ip,
                null as Date?,
                source.name,
                null as Date?,
                reason?.string
            )
            ipbanlist.addEntry(ipbanentry)
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.banip.success",
                    ip,
                    ipbanentry.banReason
                ), true
            )
            if (list.isNotEmpty()) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.banip.info",
                        list.size,
                        EntitySelector.joinNames(list)
                    ), true
                )
            }
            for (serverplayerentity in list) {
                serverplayerentity.connection.disconnect(
                    TranslationTextComponent("multiplayer.disconnect.ip_banned")
                )
            }
            list.size
        }
    }
}

