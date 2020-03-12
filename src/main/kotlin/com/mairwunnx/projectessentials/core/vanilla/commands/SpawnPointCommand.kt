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
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object SpawnPointCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.spawnpoint + "spawnpoint"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["spawnpoint"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/spawnpoint` vanilla command")
        NativeCommandUtils.removeCommand("spawnpoint")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198697_0_ ->
                    setSpawnPoint(
                        p_198697_0_.source,
                        setOf(p_198697_0_.source.asPlayer()),
                        BlockPos(p_198697_0_.source.pos)
                    )
                }.then(
                    Commands.argument(
                        "targets", EntityArgument.players()
                    ).executes { p_198694_0_ ->
                        setSpawnPoint(
                            p_198694_0_.source,
                            EntityArgument.getPlayers(p_198694_0_, "targets"),
                            BlockPos(p_198694_0_.source.pos)
                        )
                    }.then(
                        Commands.argument(
                            "pos", BlockPosArgument.blockPos()
                        ).executes { p_198698_0_ ->
                            setSpawnPoint(
                                p_198698_0_.source,
                                EntityArgument.getPlayers(p_198698_0_, "targets"),
                                BlockPosArgument.getBlockPos(p_198698_0_, "pos")
                            )
                        }
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.spawnpoint", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "spawnpoint")
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
                                "native.spawnpoint", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun setSpawnPoint(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        pos: BlockPos
    ): Int {
        checkPermissions(source)

        for (serverplayerentity in targets) {
            @Suppress("DEPRECATION")
            serverplayerentity.func_226560_a_(pos, true, false)
        }

        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.spawnpoint.success.single",
                    pos.x, pos.y, pos.z,
                    targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.spawnpoint.success.multiple",
                    pos.x, pos.y, pos.z,
                    targets.size
                ), true
            )
        }
        return targets.size
    }
}

