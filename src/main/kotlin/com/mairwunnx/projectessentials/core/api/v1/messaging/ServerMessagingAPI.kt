@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.messaging

import org.apache.logging.log4j.LogManager

/**
 * Provides API for messaging with server.
 * @since 2.0.0-SNAPSHOT.1.
 */
object ServerMessagingAPI {
    private val logger = LogManager.getLogger()

    /**
     * Throw in console warning message with reason:
     * only player can execute command.
     *
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun throwOnlyPlayerCan() = logger.warn("> Only player can execute this command")

    /**
     * Send response message on something to server console.
     * @param message response message.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun response(message: String) = logger.info("> $message")

    /**
     * Send response message on something to server console.
     * @param message response message.
     * @since 2.0.0-SNAPSHOT.2.
     */
    fun response(message: () -> String) = logger.info("> ${message()}")

    /**
     * Send list like response to server.
     *
     * For example, list all player homes or
     * warps or permissions, etc.
     *
     * @param list list to display in server console.
     * @param title list title, list name or something like that.
     * @since 2.0.0-RC.3.
     */
    fun listAsResponse(list: List<String>, title: () -> String) = response {
        """
    ${title()}

${list.joinToString(separator = ",\n") { "    > $it" }}
        """
    }
}
