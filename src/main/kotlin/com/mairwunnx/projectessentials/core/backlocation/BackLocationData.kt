package com.mairwunnx.projectessentials.core.backlocation

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.world.server.ServerWorld

/**
 * Back location data for BackLocationProvider.
 */
data class BackLocationData(
    /**
     * Server player entity class instance.
     */
    val player: ServerPlayerEntity,
    /**
     * Player world at the time of teleportation.
     */
    val world: ServerWorld = player.serverWorld,
    /**
     * Player position at the time of teleportation.
     */
    val position: Position = Position(
        player.positionVec.x, player.positionVec.y, player.positionVec.z
    ),
    /**
     * Player camera head at the time of teleportation.
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
