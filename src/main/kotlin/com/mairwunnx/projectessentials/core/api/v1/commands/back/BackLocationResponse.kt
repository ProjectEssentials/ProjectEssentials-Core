package com.mairwunnx.projectessentials.core.api.v1.commands.back

/**
 * Enum class of back location responses.
 * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
 */
enum class BackLocationResponse {
    /**
     * If back location exist and able to teleport.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    SUCCESS,

    /**
     * If back location not exist.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    ALREADY_REVOKED,

    /**
     * If back location expired and not able for teleport.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    @Suppress("unused")
    EXPIRED // todo: implement it, if community it need.
}
