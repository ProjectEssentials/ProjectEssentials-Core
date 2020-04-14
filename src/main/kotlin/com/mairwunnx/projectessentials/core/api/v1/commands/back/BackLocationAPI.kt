@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.commands.back

import net.minecraft.entity.player.ServerPlayerEntity

// todo: time based back location if community it feature need.

/**
 * Back location provider for other teleport commands,
 * for creating ability to rollback location.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
object BackLocationAPI {
    private val commits = hashMapOf<String, BackLocationData>()

    /**
     * Commits back ticket with player data in hashmap.
     * @param player server player entity instance.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    fun commit(player: ServerPlayerEntity) {
        if (exist(player)) revoke(player)
        commits[player.name.string] = BackLocationData(player)
    }

    /**
     * @param player server player entity instance.
     * @return `BackLocationData` data class instance
     * (nullable), check before using.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    fun take(player: ServerPlayerEntity) = commits[player.name.string]

    /**
     * Removing player ticket from commits hashmap.
     * @param player server player entity instance.
     * @return response as BackLocationResponse enum
     * element.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    fun revoke(player: ServerPlayerEntity) =
        if (exist(player)) {
            commits.remove(player.name.string)
            BackLocationResponse.SUCCESS
        } else {
            BackLocationResponse.ALREADY_REVOKED
        }

    /**
     * @param player server player entity instance.
     * @return `true` if player back ticket exist, `false` otherwise.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    fun exist(player: ServerPlayerEntity) = commits.containsKey(player.name.string)
}
