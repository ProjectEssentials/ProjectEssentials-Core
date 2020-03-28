package com.mairwunnx.projectessentials.core.api.v1.messaging

import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent

/**
 * This class contains all methods for interacting
 * with player messages.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
object MessagingAPI {
    /**
     * Send message to player with localized string
     * or simple message.
     * @param player target player, server player instance.
     * @param l10nString localization string or message.
     * @param isClientLocalized if true then localization
     * will provided by client \ client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun sendMessage(
        player: ServerPlayerEntity,
        l10nString: String,
        isClientLocalized: Boolean,
        vararg args: String,
        argumentChar: Char = 's'
    ) = player.sendMessage(
        if (isClientLocalized) {
            TranslationTextComponent(l10nString, *args)
        } else {
            TextComponentUtils.toTextComponent {
                val msg = LocalizationAPI.getLocalizedString(
                    player.language, l10nString, *args, argumentChar = argumentChar
                )

                if (msg.isEmpty()) {
                    return@toTextComponent l10nString
                } else {
                    return@toTextComponent msg
                }
            }
        }
    )

    /**
     * Send message to all player on server with localized
     * string or simple message.
     * @param server minecraft server instance.
     * @param l10nString localization string or message.
     * @param isClientLocalized if true then localization
     * will provided by client \ client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun sendMessageToAll(
        server: MinecraftServer,
        l10nString: String,
        isClientLocalized: Boolean,
        vararg args: String,
        argumentChar: Char = 's'
    ) = server.playerList.players.forEach {
        sendMessage(
            it, l10nString, isClientLocalized, *args, argumentChar = argumentChar
        )
    }

    /**
     * Send message to all player in specified world id
     * with localized string or simple message.
     * @param worldId target world id.
     * @param server minecraft server instance.
     * @param l10nString localization string or message.
     * @param isClientLocalized if true then localization
     * will provided by client \ client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun sendMessageToAllInWorld(
        worldId: Int,
        server: MinecraftServer,
        l10nString: String,
        isClientLocalized: Boolean,
        vararg args: String,
        argumentChar: Char = 's'
    ) = server.playerList.players.forEach {
        if (it.serverWorld.dimension.type.id == worldId) {
            sendMessage(
                it, l10nString, isClientLocalized, *args, argumentChar = argumentChar
            )
        }
    }

    /**
     * Send message to player action bar with localized
     * string or simple message.
     * @param player target player, server player instance.
     * @param l10nString localization string or message.
     * @param isClientLocalized if true then localization
     * will provided by client \ client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun sendActionBarMessage(
        player: ServerPlayerEntity,
        l10nString: String,
        isClientLocalized: Boolean,
        vararg args: String,
        argumentChar: Char = 's'
    ) = player.sendStatusMessage(
        if (isClientLocalized) {
            TranslationTextComponent(l10nString, *args)
        } else {
            TextComponentUtils.toTextComponent {
                val msg = LocalizationAPI.getLocalizedString(
                    player.language, l10nString, *args, argumentChar = argumentChar
                )

                if (msg.isEmpty()) {
                    return@toTextComponent l10nString
                } else {
                    return@toTextComponent msg
                }
            }
        }, true
    )
}