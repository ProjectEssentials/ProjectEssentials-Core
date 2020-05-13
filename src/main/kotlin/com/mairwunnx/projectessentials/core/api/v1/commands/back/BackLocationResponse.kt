package com.mairwunnx.projectessentials.core.api.v1.commands.back

/**
 * Enum class of back location responses.
 * @since 2.0.0-SNAPSHOT.1.
 */
enum class BackLocationResponse {
    /**
     * If back location exist and able to teleport.
     * @since 2.0.0-SNAPSHOT.1.
     */
    SUCCESS,

    /**
     * If back location not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    ALREADY_REVOKED,

    /**
     * If back location expired and not able for teleport.
     * @since 2.0.0-SNAPSHOT.1.
     */
    @Suppress("unused")
    EXPIRED // todo: implement it, if community it need.
}
