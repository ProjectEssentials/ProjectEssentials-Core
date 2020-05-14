package com.mairwunnx.projectessentials.core.api.v1.permissions.strategy

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.server.permission.DefaultPermissionHandler
import net.minecraftforge.server.permission.PermissionAPI

/**
 * Default permission resolution strategy.
 *
 * Checks permission node existing, and
 * if permission node not exist, will check
 * operator level.
 *
 * @since 2.0.0-RC.2.
 */
class DefaultPermissionResolutionStrategy : IPermissionResolutionStrategy {
    override fun hasPermission(
        player: ServerPlayerEntity, node: String, opLevel: Int
    ) = if (PermissionAPI.getPermissionHandler() is DefaultPermissionHandler) {
        when {
            PermissionAPI.hasPermission(player, node) -> true
            else -> player.hasPermissionLevel(opLevel)
        }
    } else {
        PermissionAPI.hasPermission(player, node)
    }
}
