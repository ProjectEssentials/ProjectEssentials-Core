package com.mairwunnx.projectessentials.core.api.v1.permissions.strategy

import net.minecraft.entity.player.ServerPlayerEntity

/**
 * Permission resolution strategy base interface
 * for classes.
 *
 * @since 2.0.0-RC.2.
 */
interface IPermissionResolutionStrategy {
    /**
     * @param player [ServerPlayerEntity] class instance.
     * @param node permission node to check.
     * @param opLevel callback operator level, for additional checking.
     * @return true if user has permission node
     * or op level *(depends on resolution strategy)*.
     *
     * @since 2.0.0-RC.2.
     */
    fun hasPermission(
        player: ServerPlayerEntity, node: String, opLevel: Int
    ): Boolean
}
