package com.mairwunnx.projectessentials.core.api.v1.messaging

import org.apache.logging.log4j.LogManager

/**
 * Provides API for messaging with server.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
object ServerMessagingAPI {
    private val logger = LogManager.getLogger()

    /**
     * Throw in console warning message with reason:
     * only player can execute command.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun throwOnlyPlayerCan() = logger.warn(
        "> Only player can execute this command"
    )
}
