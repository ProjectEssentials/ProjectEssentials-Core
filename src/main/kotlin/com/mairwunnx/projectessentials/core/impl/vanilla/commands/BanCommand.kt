/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands


import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.GameProfileArgument
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.server.management.ProfileBanEntry
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import java.util.*

internal object BanCommand : VanillaCommandBase() {
    private var aliases = configuration.take().aliases.ban + "ban"

    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.ban.failed")
    )

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["ban"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("ban")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).requires { p_198238_0_ ->
                    p_198238_0_.server.playerList.bannedPlayers.isLanServer
                }.then(
                    Commands.argument(
                        "targets",
                        GameProfileArgument.gameProfile()
                    ).executes { p_198234_0_ ->
                        banGameProfiles(
                            p_198234_0_.source,
                            GameProfileArgument.getGameProfiles(p_198234_0_, "targets"),
                            null as ITextComponent?
                        )
                    }.then(
                        Commands.argument(
                            "reason",
                            MessageArgument.message()
                        ).executes { p_198237_0_ ->
                            banGameProfiles(
                                p_198237_0_.source,
                                GameProfileArgument.getGameProfiles(p_198237_0_, "targets"),
                                MessageArgument.getMessage(p_198237_0_, "reason")
                            )
                        }
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.moderation.ban", 3)) {
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
                                "native.moderation.ban", "3"
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
    private fun banGameProfiles(
        source: CommandSource,
        gameProfiles: Collection<GameProfile>,
        reason: ITextComponent?
    ): Int {
        checkPermissions(source)

        val banlist = source.server.playerList.bannedPlayers
        var i = 0
        for (gameprofile in gameProfiles) {
            if (!banlist.isBanned(gameprofile)) {
                val profilebanentry = ProfileBanEntry(
                    gameprofile,
                    null as Date?,
                    source.name,
                    null as Date?,
                    reason?.string
                )
                banlist.addEntry(profilebanentry)
                ++i
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.ban.success",
                        TextComponentUtils.getDisplayName(gameprofile),
                        profilebanentry.banReason
                    ), true
                )
                val serverplayerentity =
                    source.server.playerList.getPlayerByUUID(gameprofile.id)
                serverplayerentity?.connection?.disconnect(TranslationTextComponent("multiplayer.disconnect.banned"))
            }
        }
        return if (i == 0) {
            throw FAILED_EXCEPTION.create()
        } else {
            i
        }
    }
}
