@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.helpers

/**
 * Error message with reason: not have permissions.
 * @since 1.14.4-1.0.0.0
 */
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
const val ONLY_PLAYER_CAN =
    "Server failed to executing \"/%0\" command".plus(
        "\n    - Reason: command should only be used by the player."
    ).plus(
        "\n    - Solution: try use command with argument or target."
    )
