package com.mairwunnx.projectessentials.core.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_CORE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.Command
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.commands.back.BackLocationAPI
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.api.v1.extensions.sendMessage
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

@Command("back")
internal object BackLocationCommand : CommandBase(literal("back")) {
    private val generalConfiguration by lazy {
        ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")
    }

    init {
        data = getData(this.javaClass)
    }

    override fun process(context: CommandContext<CommandSource>): Int {
        if (context.isPlayerSender()) {
            val player = context.getPlayer()!!

            if (
                hasPermission(player, "ess.teleport.back", 2) ||
                hasPermission(player, "ess.back", 2)
            ) {
                val data = BackLocationAPI.take(player)

                if (data != null) {
                    val pos = data.position
                    val rot = data.rotation

                    player.teleport(
                        data.world, pos.xPos, pos.yPos, pos.zPos, rot.yaw, rot.pitch
                    )

                    BackLocationAPI.revoke(player)

                    player.sendMessage(
                        "$MESSAGE_CORE_PREFIX.back.success",
                        generalConfiguration.getBoolOrDefault(SETTING_LOC_ENABLED, false)
                    )
                } else {
                    player.sendMessage(
                        "$MESSAGE_CORE_PREFIX.back.tickets_not_exists",
                        generalConfiguration.getBoolOrDefault(SETTING_LOC_ENABLED, false)
                    )
                }

                super.process(context)
            } else {
                player.sendMessage(
                    "$MESSAGE_CORE_PREFIX.back.restricted",
                    generalConfiguration.getBoolOrDefault(SETTING_LOC_ENABLED, false)
                )
            }
        } else {
            ServerMessagingAPI.throwOnlyPlayerCan()
        }
        return 0
    }
}
