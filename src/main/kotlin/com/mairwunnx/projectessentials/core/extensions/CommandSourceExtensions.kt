@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.extensions

import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent

/**
 * Send localized message to player without logging.
 * @param moduleName mod module name.
 * @param commandSource command source instance.
 * @param l10nString localized string without `project_essentials_`.
 * @param args additional arguments for localized string `(%s literals)`.
 * @since 1.14.4-1.0.0.0
 */
fun sendMsg(
    moduleName: String,
    commandSource: CommandSource,
    l10nString: String,
    vararg args: String
) {
    commandSource.sendFeedback(
        TranslationTextComponent(
            "project_essentials_$moduleName.$l10nString", *args
        ), false
    )
}
