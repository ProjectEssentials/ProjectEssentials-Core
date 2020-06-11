package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

abstract class VanillaCommandBase(val name: String) {
    open fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand(name)
        LogManager.getLogger().info("Native command replacing implementation for $name")
    }

    companion object {
        @JvmStatic
        fun isAllowed(source: CommandSource, node: String, opLevel: Int) =
            if (source.entity is ServerPlayerEntity) {
                hasPermission(source.asPlayer(), "native.$node", opLevel)
            } else true

        @JvmStatic
        fun isAllowedAny(source: CommandSource, notation: () -> List<Pair<String, Int>>) =
            if (source.entity is ServerPlayerEntity) {
                notation().forEach {
                    if (hasPermission(source.asPlayer(), it.first, it.second)) return true
                }.let { false }
            } else true
    }
}
