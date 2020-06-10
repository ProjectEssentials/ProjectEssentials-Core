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
import net.minecraft.command.impl.SaveAllCommand

internal object SaveAllCommand : VanillaCommandBase("save-all") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher)
        dispatcher.register(
            Commands.literal(name).executes {
                ConfigurationAPI.saveAll()
                SaveAllCommand.saveAll(it.source, false)
            }.then(
                Commands.literal("flush").executes {
                    ConfigurationAPI.saveAll()
                    SaveAllCommand.saveAll(it.source, true)
                }
            )
        )
    }
}
