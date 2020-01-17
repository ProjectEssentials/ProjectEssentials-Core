@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.GameProfileArgument
import net.minecraft.server.management.WhitelistEntry
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.lang.String

object WhitelistCommand {
    private val ALREADY_ON = SimpleCommandExceptionType(
        TranslationTextComponent("commands.whitelist.alreadyOn")
    )
    private val ALREADY_OFF = SimpleCommandExceptionType(
        TranslationTextComponent("commands.whitelist.alreadyOff")
    )
    private val PLAYER_ALREADY_WHITELISTED = SimpleCommandExceptionType(
        TranslationTextComponent("commands.whitelist.add.failed")
    )
    private val PLAYER_NOT_WHITELISTED = SimpleCommandExceptionType(
        TranslationTextComponent("commands.whitelist.remove.failed")
    )
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/whitelist` vanilla command")

        dispatcher.register(
            Commands.literal("whitelist").then(
                Commands.literal("on").executes { p_198872_0_ ->
                    enableWhiteList(p_198872_0_.source)
                }
            ).then(
                Commands.literal("off").executes { p_198874_0_ ->
                    disableWhiteList(p_198874_0_.source)
                }
            ).then(
                Commands.literal("list").executes { p_198878_0_ ->
                    listWhitelistedPlayers(p_198878_0_.source)
                }
            ).then(
                Commands.literal("add").then(
                    Commands.argument(
                        "targets", GameProfileArgument.gameProfile()
                    ).suggests { p_198879_0_, p_198879_1_ ->
                        val playerlist = p_198879_0_.source.server.playerList
                        ISuggestionProvider.suggest(
                            playerlist.players.stream().filter { p_198871_1_ ->
                                !playerlist.whitelistedPlayers.isWhitelisted(
                                    p_198871_1_.gameProfile
                                )
                            }.map { p_200567_0_ ->
                                p_200567_0_.gameProfile.name
                            }, p_198879_1_
                        )
                    }.executes { p_198875_0_ ->
                        addPlayers(
                            p_198875_0_.source,
                            GameProfileArgument.getGameProfiles(p_198875_0_, "targets")
                        )
                    }
                )
            ).then(
                Commands.literal("remove").then(
                    Commands.argument(
                        "targets", GameProfileArgument.gameProfile()
                    ).suggests { p_198881_0_, p_198881_1_ ->
                        ISuggestionProvider.suggest(
                            p_198881_0_.source.server.playerList.whitelistedPlayerNames,
                            p_198881_1_
                        )
                    }.executes { p_198870_0_ ->
                        removePlayers(
                            p_198870_0_.source,
                            GameProfileArgument.getGameProfiles(p_198870_0_, "targets")
                        )
                    }
                )
            ).then(
                Commands.literal("reload").executes { p_198882_0_ ->
                    reload(p_198882_0_.source)
                }
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.whitelist", 3)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "whitelist")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.whitelist.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun reload(source: CommandSource): Int {
        checkPermissions(source)
        source.server.playerList.reloadWhitelist()
        source.sendFeedback(
            TranslationTextComponent(
                "commands.whitelist.reloaded"
            ), true
        )
        source.server.kickPlayersNotWhitelisted(source)
        return 1
    }

    @Throws(CommandSyntaxException::class)
    private fun addPlayers(
        source: CommandSource,
        players: Collection<GameProfile>
    ): Int {
        checkPermissions(source)

        val whitelist = source.server.playerList.whitelistedPlayers
        var i = 0
        for (gameprofile in players) {
            if (!whitelist.isWhitelisted(gameprofile)) {
                val whitelistentry = WhitelistEntry(gameprofile)
                whitelist.addEntry(whitelistentry)
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.whitelist.add.success",
                        TextComponentUtils.getDisplayName(gameprofile)
                    ), true
                )
                ++i
            }
        }
        return if (i == 0) {
            throw PLAYER_ALREADY_WHITELISTED.create()
        } else {
            i
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun removePlayers(
        source: CommandSource,
        players: Collection<GameProfile>
    ): Int {
        checkPermissions(source)

        val whitelist = source.server.playerList.whitelistedPlayers
        var i = 0
        for (gameprofile in players) {
            if (whitelist.isWhitelisted(gameprofile)) {
                val whitelistentry = WhitelistEntry(gameprofile)
                whitelist.removeEntry(whitelistentry)
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.whitelist.remove.success",
                        TextComponentUtils.getDisplayName(gameprofile)
                    ), true
                )
                ++i
            }
        }
        return if (i == 0) {
            throw PLAYER_NOT_WHITELISTED.create()
        } else {
            source.server.kickPlayersNotWhitelisted(source)
            i
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun enableWhiteList(source: CommandSource): Int {
        checkPermissions(source)

        val playerlist = source.server.playerList
        return if (playerlist.isWhiteListEnabled) {
            throw ALREADY_ON.create()
        } else {
            playerlist.isWhiteListEnabled = true
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.whitelist.enabled"
                ), true
            )
            source.server.kickPlayersNotWhitelisted(source)
            1
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun disableWhiteList(source: CommandSource): Int {
        checkPermissions(source)

        val playerlist = source.server.playerList
        return if (!playerlist.isWhiteListEnabled) {
            throw ALREADY_OFF.create()
        } else {
            playerlist.isWhiteListEnabled = false
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.whitelist.disabled"
                ), true
            )
            1
        }
    }

    private fun listWhitelistedPlayers(source: CommandSource): Int {
        checkPermissions(source)

        val astring =
            source.server.playerList.whitelistedPlayerNames
        if (astring.isEmpty()) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.whitelist.none"
                ), false
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.whitelist.list",
                    astring.size,
                    String.join(", ", *astring)
                ), false
            )
        }
        return astring.size
    }
}
