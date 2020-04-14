@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.messaging

import org.apache.logging.log4j.LogManager

/**
 * Provides API for messaging with server.
 * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
 */
object ServerMessagingAPI {
    private val logger = LogManager.getLogger()

    /**
     * Throw in console warning message with reason:
     * only player can execute command.
     *
     * todo: add localization to this message.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun throwOnlyPlayerCan() = logger.warn("> Only player can execute this command")

    /**
     * Send response message on something to server console.
     * @param message response message.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun response(message: String) = logger.info("> $message")
}
