package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class VanillaCommandBase(val name: String) {
    val logger: Logger = LogManager.getLogger()
    open fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand(name)
    }
}
