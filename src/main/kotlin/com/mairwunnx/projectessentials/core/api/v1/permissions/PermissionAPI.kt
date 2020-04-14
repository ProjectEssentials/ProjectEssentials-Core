package com.mairwunnx.projectessentials.core.api.v1.permissions

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.server.permission.PermissionAPI

/**
 * Uses installed forge permissions provider for
 * checking permissions.
 *
 * This like extension but it also checks operator level
 * if permission node not exist.
 *
 * @param player target player for checking
 * permissions.
 * @param node required permission node.
 * @param opLevel required operator level.
 * @return true if player has permission.
 * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
 */
fun hasPermission(
    player: ServerPlayerEntity,
    node: String,
    opLevel: Int
) = when {
    PermissionAPI.hasPermission(player, node) -> true
    else -> player.hasPermissionLevel(opLevel)
}
