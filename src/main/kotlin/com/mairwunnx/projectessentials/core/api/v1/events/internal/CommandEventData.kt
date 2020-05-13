package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.commands.ICommand
import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData

/**
 * Command event data, stores command instance.
 * @since 2.0.0-SNAPSHOT.1.
 */
class CommandEventData(val command: ICommand) : IModuleEventData
