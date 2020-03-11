@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.helpers

import org.apache.logging.log4j.LogManager

/**
 * Error message with reason: not have permissions.
 * @since 1.14.4-1.0.0.0
 */
@Deprecated("Instead this use `throwPermissionLevel` method.")
const val PERMISSION_LEVEL =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: permission level executing command more than player permission level."
    )

/**
 * Error message with reason: command cooldown not expired.
 * @since 1.14.4-1.0.0.0
 */
const val COOLDOWN_NOT_EXPIRED =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: command cooldown not expired."
    )

/**
 * Error message with reason: typed command disabled.
 * @since 1.14.4-1.0.0.0
 */
const val DISABLED_COMMAND =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: it command disabled by mod configuration."
    )

/**
 * Error message with reason: disabled command argument.
 * @since 1.14.4-1.0.0.0
 */
const val DISABLED_COMMAND_ARG =
    "Player (%0) failed to executing \"/%1\" command".plus(
        "\n    - Reason: arguments for it command disabled by mod configuration."
    )

/**
 * Error message with reason: only player can execute command.
 * @since 1.14.4-1.0.0.0
 */
@Deprecated("Instead this use `throwOnlyPlayerCan` method.")
const val ONLY_PLAYER_CAN =
    "Server failed to executing \"/%0\" command".plus(
        "\n    - Reason: command should only be used by the player."
    ).plus(
        "\n    - Solution: try use command with argument or target."
    )

private val logger = LogManager.getLogger()

/**
 * Throw in console warning message with reason:
 * only player can execute command.
 *
 * @param command executed command name.
 *
 * @since 1.14.4-1.3.0
 */
fun throwOnlyPlayerCan(command: String) = logger.warn(
    "\n  Server failed to executing \"/$command\" command".plus(
        "\n    - Reason: command should only be used by the player."
    ).plus(
        "\n    - Solution: try use command with argument or target."
    )
)

/**
 * Throw in console warning message with reason:
 * player don't have permissions.
 *
 * @param playerName player nickname represented as string.
 * @param command executed command name.
 *
 * @since 1.14.4-1.3.0
 */
fun throwPermissionLevel(playerName: String, command: String) = logger.warn(
    "Player ($playerName) failed to executing \"/$command\" command".plus(
        "\n    - Reason: permission level executing command more than player permission level."
    )
)
