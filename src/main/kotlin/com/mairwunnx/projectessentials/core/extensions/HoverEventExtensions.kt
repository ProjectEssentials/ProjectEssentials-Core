package com.mairwunnx.projectessentials.core.extensions

import com.mairwunnx.projectessentials.core.localization.getLocalizedString
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent

/**
 * @param player server player entity instance.
 * @param safeLocalization if safe localization
 * enabled then will be used non-client localization.
 * @param l10n localization string for indexing localized
 * string.
 * @param args additional arguments for localized string.
 *
 * @return HoverEvent instance with your localized string.
 *
 * @since 1.14.4-1.3.0
 */
fun hoverEventFrom(
    player: ServerPlayerEntity,
    safeLocalization: Boolean,
    l10n: String,
    vararg args: String
): HoverEvent = if (safeLocalization) {
    HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        TextComponentUtils.toTextComponent {
            getLocalizedString(player, l10n, *args)
        }
    )
} else {
    HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        TranslationTextComponent(l10n, args)
    )
}
