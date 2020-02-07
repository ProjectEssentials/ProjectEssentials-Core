package com.mairwunnx.projectessentials.core

import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import net.minecraft.entity.player.ServerPlayerEntity

internal object JavaCompatibility {
    fun hasPermission(
        player: ServerPlayerEntity,
        node: String,
        opLevel: Int
    ): Boolean = if (EntryPoint.permissionsInstalled) {
        PermissionsAPI.hasPermission(player.name.string, node)
    } else {
        player.hasPermissionLevel(opLevel)
    }
}
