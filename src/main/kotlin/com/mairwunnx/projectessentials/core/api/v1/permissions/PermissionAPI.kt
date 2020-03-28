package com.mairwunnx.projectessentials.core.api.v1.permissions

import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * If permissions api module installed
 * then for checking permissions will be used
 * `node` if permissions module not installed
 * then will be used `opLevel` for checking
 * permissions.
 *
 * @param player target player for checking
 * permissions.
 * @param node required permission node.
 * @param opLevel required operator level.
 * @return true if player has permission.
 * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
 */
fun hasPermission(
    player: ServerPlayerEntity,
    node: String,
    opLevel: Int
): Boolean = if (ModuleAPI.isModuleExist("permissions")) {
    PermissionsAPI.hasPermission(player.name.string, node)
} else {
    player.hasPermissionLevel(opLevel)
}
