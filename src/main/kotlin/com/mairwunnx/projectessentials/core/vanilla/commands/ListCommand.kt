/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.util.function.Function

internal object ListCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.list + "list"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["list"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/list` vanilla command")
        NativeCommandUtils.removeCommand("list")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198523_0_ ->
                    listNames(p_198523_0_.source)
                }.then(
                    Commands.literal("uuids").executes { p_208202_0_ ->
                        listUUIDs(p_208202_0_.source)
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.list", 0)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "list")
                )
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        LocalizationConfigurationUtils.getConfig().enabled,
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                LocalizationConfigurationUtils.getConfig().enabled,
                                "native.command.restricted_hover",
                                "native.list", "0"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun listNames(source: CommandSource): Int {
        checkPermissions(source)

        return listPlayers(source,
            Function { obj: ServerPlayerEntity -> obj.displayName }
        )
    }

    private fun listUUIDs(source: CommandSource): Int {
        checkPermissions(source)

        return listPlayers(source,
            Function { obj: ServerPlayerEntity -> obj.displayNameAndUUID }
        )
    }

    private fun listPlayers(
        source: CommandSource,
        nameExtractor: Function<ServerPlayerEntity, ITextComponent>
    ): Int {
        checkPermissions(source)

        val playerlist = source.server.playerList
        val list = playerlist.players
        val itextcomponent = TextComponentUtils.makeList(list, nameExtractor)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.list.players",
                list.size,
                playerlist.maxPlayers,
                itextcomponent
            ), false
        )
        return list.size
    }
}

