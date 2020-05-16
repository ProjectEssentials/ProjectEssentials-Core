package com.mairwunnx.projectessentials.core.api.v1.permissions

import com.mairwunnx.projectessentials.core.api.v1.permissions.strategy.DefaultPermissionResolutionStrategy
import com.mairwunnx.projectessentials.core.api.v1.permissions.strategy.IPermissionResolutionStrategy
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * Permission resolution strategy.
 *
 * By default uses default resolution strategy
 * [DefaultPermissionResolutionStrategy] class.
 *
 * You can change resolution strategy at any time.
 *
 * @since 2.0.0-RC.2.
 */
var permissionResolutionStrategy: IPermissionResolutionStrategy =
    DefaultPermissionResolutionStrategy()

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
 * @since 2.0.0-SNAPSHOT.1.
 */
fun hasPermission(
    player: ServerPlayerEntity, node: String, opLevel: Int
) = permissionResolutionStrategy.hasPermission(player, node, opLevel)
