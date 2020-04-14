package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.commands.ICommand
import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData

/**
 * Command event data, stores command instance.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
class CommandEventData(val command: ICommand) : IModuleEventData
