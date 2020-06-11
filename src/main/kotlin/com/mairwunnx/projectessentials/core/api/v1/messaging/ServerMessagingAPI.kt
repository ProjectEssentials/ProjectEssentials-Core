@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.messaging

import com.mairwunnx.projectessentials.core.api.v1.SETTING_ENABLE_CONSOLE_COLORS
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.impl.generalConfiguration
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
    fun throwOnlyPlayerCan() {
        if (!generalConfiguration.getBool(SETTING_ENABLE_CONSOLE_COLORS)) {
            logger.warn("> Only player can execute this command")
        } else {
            logger.warn("§7> §cOnly player can execute this command")
        }
    }

    /**
     * Send response message on something to server console.
     * @param message response message.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun response(message: String) = response { message }

    /**
     * Send response message on something to server console.
     * @param message response message.
     * @since 2.0.0-SNAPSHOT.2.
     */
    fun response(message: () -> String) = logger.info(
        "§7> §r${if (!generalConfiguration.getBool(SETTING_ENABLE_CONSOLE_COLORS)) {
            message().replace(Regex("[&§][0-9a-fk-or]"), String.empty)
        } else {
            message().replace("&", "§")
        }}"
    )

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
    §6${title()}

${list.joinToString(separator = ",\n") { "    §7> §c$it" }}
        """
    }
}
