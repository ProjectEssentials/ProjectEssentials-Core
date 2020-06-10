/**
 * ! This command implementation by Mojang Game Studios!
 *
 * Decompiled with idea source code was converted to kotlin code.
 * But with additions such as permissions checking and etc.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */
package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.TranslationTextComponent

internal object ReloadCommand : VanillaCommandBase("reload") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(
            Commands.literal(name).executes {
                it.source.sendFeedback(TranslationTextComponent("commands.reload.success"), true)
                it.source.server.reload().also { ConfigurationAPI.reloadAll() }.let { 0 }
            }
        )
    }
}
