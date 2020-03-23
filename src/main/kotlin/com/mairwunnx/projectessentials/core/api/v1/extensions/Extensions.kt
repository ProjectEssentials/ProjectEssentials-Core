@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.extensions

import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer

/**
 * Send message to player with localized string
 * or simple message.
 * @param l10nString localization string or message.
 * @param args localization string arguments.
 * @param argumentChar localization argument char.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun ServerPlayerEntity.sendMessage(
    l10nString: String,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendMessage(
    this, l10nString, *args, argumentChar = argumentChar
)

/**
 * Send message to player action bar with localized
 * string or simple message.
 * @param l10nString localization string or message.
 * @param args localization string arguments.
 * @param argumentChar localization argument char.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun ServerPlayerEntity.sendActionBarMessage(
    l10nString: String,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendActionBarMessage(
    this, l10nString, *args, argumentChar = argumentChar
)

/**
 * Send message to all player on server with localized
 * string or simple message.
 * @param l10nString localization string or message.
 * @param args localization string arguments.
 * @param argumentChar localization argument char.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun MinecraftServer.sendMessageToAll(
    l10nString: String,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendMessageToAll(
    this, l10nString, *args, argumentChar = argumentChar
)

/**
 * Send message to all player in specified world id
 * with localized string or simple message.
 * @param worldId target world id.
 * @param l10nString localization string or message.
 * @param args localization string arguments.
 * @param argumentChar localization argument char.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun MinecraftServer.sendMessageToAllInWorld(
    worldId: Int,
    l10nString: String,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendMessageToAllInWorld(
    worldId, this, l10nString, *args, argumentChar = argumentChar
)
