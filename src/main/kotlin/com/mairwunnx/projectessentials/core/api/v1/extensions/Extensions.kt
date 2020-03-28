@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.extensions

import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI.getLocalizedString
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import net.minecraftforge.event.CommandEvent

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
    isClientLocalized: Boolean,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendMessage(
    this, l10nString, isClientLocalized, *args, argumentChar = argumentChar
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
    isClientLocalized: Boolean,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendActionBarMessage(
    this, l10nString, isClientLocalized, *args, argumentChar = argumentChar
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
    isClientLocalized: Boolean,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendMessageToAll(
    this, l10nString, isClientLocalized, *args, argumentChar = argumentChar
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
    isClientLocalized: Boolean,
    vararg args: String,
    argumentChar: Char = 's'
) = MessagingAPI.sendMessageToAllInWorld(
    worldId, this, l10nString, isClientLocalized, *args, argumentChar = argumentChar
)

/**
 * @return true if command sender is player.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun CommandContext<CommandSource>.isPlayerSender() =
    this.source.entity is ServerPlayerEntity

/**
 * @return player if sender is player, if sender is
 * entity or server then return null.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun CommandContext<CommandSource>.getPlayer() =
    if (this.isPlayerSender()) this.source.asPlayer() else null

/**
 * @return if command source is player then nickname
 * from CommandContext. If command source is server
 * then return `#server`.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun CommandContext<CommandSource>.playerName(): String =
    if (this.isPlayerSender()) this.source.asPlayer().name.string else "#server"

/**
 * Return command name as string.
 *
 * Example: player execute command **`/heal MairwunNx`**,
 * then you get **`heal`** as string.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
val CommandEvent.commandName
    get() = this.executedCommand
        .replace("/", "").split(" ")[0]

/**
 * Return fully executed command as string.
 *
 * Example: player execute command **`/heal MairwunNx`**,
 * then you get **`/heal MairwunNx`** as string.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
val CommandEvent.executedCommand: String
    get() = this.parseResults.reader.string

/**
 * @return true if source is player.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun CommandEvent.isPlayerSender() = this.source.entity is ServerPlayerEntity

/**
 * Return **`ServerPlayerEntity?`** class instance from
 * **`CommandEvent`** class instance. If source is server
 * then return null.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun CommandEvent.getPlayer() =
    if (this.isPlayerSender()) this.source.asPlayer() else null

/**
 * Return command **`source`** from **`CommandEvent`**
 * class instance.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
val CommandEvent.source: CommandSource
    get() = this.parseResults.context.source

/**
 * @param player server player entity instance.
 * @param safeLocalization if safe localization
 * enabled then will be used non-client localization.
 * @param l10n localization string for indexing localized
 * string.
 * @param args additional arguments for localized string.
 * @return HoverEvent instance with your localized string.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun hoverEventFrom(
    player: ServerPlayerEntity,
    safeLocalization: Boolean,
    l10n: String,
    vararg args: String
) = if (safeLocalization) {
    HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        TextComponentUtils.toTextComponent {
            getLocalizedString(player.language, l10n, *args)
        }
    )
} else {
    HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        TranslationTextComponent(l10n, args)
    )
}

/**
 * @param player server player entity instance.
 * @param safeLocalization if safe localization
 * enabled then will be used non-client localization.
 * @param l10n localization string for indexing localized
 * string.
 * @param args additional arguments for localized string.
 * @return ITextComponent implements class instance with
 * your localized string.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun textComponentFrom(
    player: ServerPlayerEntity,
    safeLocalization: Boolean,
    l10n: String,
    vararg args: String
): ITextComponent = TextComponentUtils.toTextComponent {
    if (safeLocalization) {
        getLocalizedString(player.language, l10n, *args)
    } else {
        TranslationTextComponent(l10n, args).formattedText
    }
}

/**
 * Capitalize each word in string.
 * @return capitalized each word string.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun String.capitalizeWords() =
    split(" ").joinToString(" ") { it.capitalize() }

/**
 * Return empty string.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
val String.Companion.empty get() = ""
