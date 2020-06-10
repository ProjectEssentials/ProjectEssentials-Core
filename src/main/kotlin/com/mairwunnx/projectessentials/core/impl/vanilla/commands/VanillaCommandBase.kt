package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource

abstract class VanillaCommandBase(val name: String) {
    open fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand(name)
    }
}
