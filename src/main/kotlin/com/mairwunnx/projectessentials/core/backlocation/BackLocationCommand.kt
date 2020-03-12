package com.mairwunnx.projectessentials.core.backlocation

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.extensions.playerName
import com.mairwunnx.projectessentials.core.extensions.sendMsg
import com.mairwunnx.projectessentials.core.helpers.throwOnlyPlayerCan
import com.mairwunnx.projectessentials.core.helpers.throwPermissionLevel
import com.mairwunnx.projectessentials.core.localization.sendMsgV2
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

internal object BackLocationCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Registering `/back` command")
        dispatcher.register(literal<CommandSource>("back").executes(::execute))
    }

    private fun execute(context: CommandContext<CommandSource>): Int {
        if (context.isPlayerSender()) {
            val player = context.source.asPlayer()
            val prefix = "project_essentials_core"

            if (EntryPoint.hasPermission(player, "teleport.back", 2)) {
                val data = BackLocationProvider.take(player)

                if (data != null) {
                    val pos = data.position
                    val rot = data.rotation

                    player.teleport(
                        data.world, pos.xPos, pos.yPos, pos.zPos, rot.yaw, rot.pitch
                    )

                    BackLocationProvider.revoke(player)

                    if (LocalizationConfigurationUtils.getConfig().enabled) {
                        sendMsgV2(player, "$prefix.back.success")
                    } else {
                        sendMsg("core", context.source, "back.success")
                    }
                } else {
                    if (LocalizationConfigurationUtils.getConfig().enabled) {
                        sendMsgV2(player, "$prefix.back.tickets_not_exists")
                    } else {
                        sendMsg("core", context.source, "back.tickets_not_exists")
                    }
                }

                logger.info("Executed command \"/back\" from ${context.playerName()}")
            } else {
                throwPermissionLevel(context.playerName(), "back")

                if (LocalizationConfigurationUtils.getConfig().enabled) {
                    sendMsgV2(player, "$prefix.back.restricted")
                } else {
                    sendMsg("core", context.source, "back.restricted")
                }
            }
        } else {
            throwOnlyPlayerCan("back")
        }

        return 0
    }
}
