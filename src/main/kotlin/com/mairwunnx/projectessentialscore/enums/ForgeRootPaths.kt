package com.mairwunnx.projectessentialscore.enums

/**
 * This mainly serves to get the true configuration path.
 * @see com.mairwunnx.projectessentialscore.helpers.getRootPath
 * @since 1.14.4-1.0.0.0
 */
enum class ForgeRootPaths {
    /**
     * When sending this enum element, the path relative
     * to the game client will be returned.
     * @see com.mairwunnx.projectessentialscore.helpers.getRootPath
     */
    CLIENT,
    /**
     * When sending this enum element, the path relative
     * to the game server will be returned.
     * @see com.mairwunnx.projectessentialscore.helpers.getRootPath
     */
    SERVER
}
