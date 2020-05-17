@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.messaging

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LIST_MAX_ELEMENTS_IN_PAGE
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.StringTextComponent
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
     * Send list like response to player in chat.
     * With pages (passed as argument in [context])
     * with name `page`.
     *
     * Max displayed lines per page defined in
     * setting `list-max-elements-in-page`.
     *
     * @param context command context.
     * @param list list to display in server console.
     * @param title list title, list name or something like that.
     * @since 2.0.0-RC.3.
     */
    fun sendListAsMessage(
        context: CommandContext<CommandSource>,
        list: List<String>,
        title: () -> String
    ) {
        require(context.isPlayerSender()) {
            "Command sender is no player, use [ServerMessagingAPI.listAsResponse] for server"
        }
        val linesPerPage = generalConfiguration.getIntOrDefault(
            SETTING_LIST_MAX_ELEMENTS_IN_PAGE, 8
        )
        val pages = list.count() / linesPerPage + 1
        val page = when {
            CommandAPI.getIntExisting(context, "page") -> {
                CommandAPI.getInt(context, "page")
            }
            else -> 1
        }
        sendListAsMessage(context.getPlayer()!!, list, page, pages, linesPerPage, title)
    }

    /**
     * Send list like response to player in chat.
     *
     * Max displayed lines per page defined in
     * setting `list-max-elements-in-page`.
     *
     * @param player player to send list.
     * @param list list to display in server console.
     * @param page list page. (unchecked page!)
     * @param maxPage pages total to can be displayed.
     * @param linesPerPage lines per page.
     * @param title list title, list name or something like that.
     * @since 2.0.0-RC.3.
     */
    fun sendListAsMessage(
        player: ServerPlayerEntity,
        list: List<String>,
        page: Int,
        maxPage: Int,
        linesPerPage: Int,
        title: () -> String
    ) {
        val displayedLines = page * linesPerPage
        val droppedLines = displayedLines - linesPerPage
        val values = list.take(displayedLines).drop(droppedLines)
        val pageString = LocalizationAPI.getLocalizedString(
            player.language,
            "project_essentials_core.simple.page",
            page.toString(),
            maxPage.toString()
        ).let {
            if (it.isEmpty()) {
                return@let TranslationTextComponent(
                    "project_essentials_core.simple.page",
                    page, maxPage
                ).formattedText
            }
            return@let it
        }
        val message =
            """
§7$title $pageString

§7${values.joinToString(separator = "\n") { "    §c> §7$it" }}
            """
        player.commandSource.sendFeedback(StringTextComponent(message), false)
    }

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
