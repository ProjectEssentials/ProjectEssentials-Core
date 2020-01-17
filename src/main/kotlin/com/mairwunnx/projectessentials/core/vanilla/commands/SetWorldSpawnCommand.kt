/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.network.play.server.SSpawnPositionPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object SetWorldSpawnCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/setworldspawn` vanilla command")

        dispatcher.register(
            Commands.literal("setworldspawn").executes { p_198700_0_ ->
                setSpawn(
                    p_198700_0_.source, BlockPos(p_198700_0_.source.pos)
                )
            }.then(
                Commands.argument(
                    "pos", BlockPosArgument.blockPos()
                ).executes { p_198703_0_ ->
                    setSpawn(
                        p_198703_0_.source, BlockPosArgument.getBlockPos(p_198703_0_, "pos")
                    )
                }
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.setworldspawn", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "setworldspawn")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.setworldspawn.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun setSpawn(source: CommandSource, pos: BlockPos): Int {
        checkPermissions(source)

        source.world.spawnPoint = pos
        source.server.playerList.sendPacketToAllPlayers(SSpawnPositionPacket(pos))
        source.sendFeedback(
            TranslationTextComponent(
                "commands.setworldspawn.success",
                pos.x, pos.y, pos.z
            ), true
        )
        return 1
    }
}
