package com.mairwunnx.projectessentials.core.api.v1.commands.back

/**
 * Enum class of back location responses.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
enum class BackLocationResponse {
    /**
     * If back location exist and able to teleport.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    SUCCESS,

    /**
     * If back location not exist.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    ALREADY_REVOKED,

    /**
     * If back location expired and not able for teleport.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    EXPIRED // todo: implement it, if community it need.
}
