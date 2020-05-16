/**
 * This command implementation by Mojang.
 * And decompiled with idea source code was converted
 * to kotlin code.
 * Also added some logic, for example checking on
 * permissions, and for some commands shorten aliases.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.network.play.server.SSpawnPositionPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object SetWorldSpawnCommand : VanillaCommandBase() {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("setworldspawn")

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
            if (!hasPermission(source.asPlayer(), "native.setworldspawn", 2)) {
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
                                "native.setworldspawn", "2"
                            )
                        )
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
