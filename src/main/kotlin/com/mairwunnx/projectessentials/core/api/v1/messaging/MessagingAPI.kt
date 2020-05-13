@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.messaging

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent

/**
 * This class contains all methods for interacting
 * with player messages.
 * @since 2.0.0-SNAPSHOT.1.
 */
object MessagingAPI {
    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }

    /**
     * Send message to player with localized string
     * or simple message.
     * @param player target player, server player instance.
     * @param l10nString localization string or message.
     * @param safeLocalization if true then localization
     * will provided by server false if localization
     * will provided by client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @param clickEvent click event for message.
     * @param hoverEvent hover event for message.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun sendMessage(
        player: ServerPlayerEntity,
        l10nString: String,
        safeLocalization: Boolean = generalConfiguration.getBool(SETTING_LOC_ENABLED),
        vararg args: String,
        argumentChar: Char = 's',
        clickEvent: ClickEvent? = null,
        hoverEvent: HoverEvent? = null
    ) = player.sendMessage(
        if (safeLocalization) {
            TextComponentUtils.toTextComponent {
                val msg = LocalizationAPI.getLocalizedString(
                    player.language, l10nString, *args, argumentChar = argumentChar
                )
                if (msg.isEmpty()) return@toTextComponent l10nString else return@toTextComponent msg
            }.apply {
                clickEvent?.let { style.clickEvent = it }
                hoverEvent?.let { style.hoverEvent = it }
            }
        } else {
            TranslationTextComponent(l10nString, *args).apply {
                clickEvent?.let { style.clickEvent = it }
                hoverEvent?.let { style.hoverEvent = it }
            }
        }
    )

    /**
     * Send message to all player on server with localized
     * string or simple message.
     * @param server minecraft server instance.
     * @param l10nString localization string or message.
     * @param safeLocalization if true then localization
     * will provided by server false if localization
     * will provided by client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @param clickEvent click event for message.
     * @param hoverEvent hover event for message.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun sendMessageToAll(
        server: MinecraftServer,
        l10nString: String,
        safeLocalization: Boolean = generalConfiguration.getBool(SETTING_LOC_ENABLED),
        vararg args: String,
        argumentChar: Char = 's',
        clickEvent: ClickEvent? = null,
        hoverEvent: HoverEvent? = null
    ) = server.playerList.players.forEach {
        sendMessage(
            player = it,
            l10nString = l10nString,
            safeLocalization = safeLocalization,
            args = *args,
            argumentChar = argumentChar,
            clickEvent = clickEvent,
            hoverEvent = hoverEvent
        )
    }

    /**
     * Send message to all player in specified world id
     * with localized string or simple message.
     * @param worldId target world id.
     * @param server minecraft server instance.
     * @param l10nString localization string or message.
     * @param safeLocalization if true then localization
     * will provided by server false if localization
     * will provided by client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @param clickEvent click event for message.
     * @param hoverEvent hover event for message.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun sendMessageToAllInWorld(
        worldId: Int,
        server: MinecraftServer,
        l10nString: String,
        safeLocalization: Boolean = generalConfiguration.getBool(SETTING_LOC_ENABLED),
        vararg args: String,
        argumentChar: Char = 's',
        clickEvent: ClickEvent? = null,
        hoverEvent: HoverEvent? = null
    ) = server.playerList.players.forEach {
        if (it.serverWorld.dimension.type.id == worldId) {
            sendMessage(
                player = it,
                l10nString = l10nString,
                safeLocalization = safeLocalization,
                args = *args,
                argumentChar = argumentChar,
                clickEvent = clickEvent,
                hoverEvent = hoverEvent
            )
        }
    }

    /**
     * Send message to player action bar with localized
     * string or simple message.
     * @param player target player, server player instance.
     * @param l10nString localization string or message.
     * @param safeLocalization if true then localization
     * will provided by server false if localization
     * will provided by client resource pack.
     * @param args localization string arguments.
     * @param argumentChar localization argument char.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun sendActionBarMessage(
        player: ServerPlayerEntity,
        l10nString: String,
        safeLocalization: Boolean = generalConfiguration.getBool(SETTING_LOC_ENABLED),
        vararg args: String,
        argumentChar: Char = 's'
    ) = player.sendStatusMessage(
        if (safeLocalization) {
            TextComponentUtils.toTextComponent {
                val msg = LocalizationAPI.getLocalizedString(
                    player.language, l10nString, *args, argumentChar = argumentChar
                )
                if (msg.isEmpty()) return@toTextComponent l10nString else return@toTextComponent msg
            }
        } else {
            TranslationTextComponent(l10nString, *args)
        }, true
    )
}
