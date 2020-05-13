package com.mairwunnx.projectessentials.core.api.v1.commands.back

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.world.server.ServerWorld

/**
 * Back location data for BackLocationAPI.
 * @since 2.0.0-SNAPSHOT.1.
 */
data class BackLocationData(
    /**
     * Server player entity class instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val player: ServerPlayerEntity,
    /**
     * Player world at the time of teleportation.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val world: ServerWorld = player.serverWorld,
    /**
     * Player position at the time of teleportation.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val position: Position = Position(
        player.posX, player.posY, player.posZ
    ),
    /**
     * Player camera head at the time of teleportation.
     * @since 2.0.0-SNAPSHOT.1.
     */
    val rotation: Rotation = Rotation(
        player.rotationYaw, player.rotationPitch
    )
) {
    data class Position(
        val xPos: Double, val yPos: Double, val zPos: Double
    )

    data class Rotation(
        val yaw: Float, val pitch: Float
    )
}
