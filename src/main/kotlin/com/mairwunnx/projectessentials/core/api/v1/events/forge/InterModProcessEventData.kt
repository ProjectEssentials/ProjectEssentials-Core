package com.mairwunnx.projectessentials.core.api.v1.events.forge

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent

/**
 * @since 2.0.0-SNAPSHOT.1.
 */
class InterModProcessEventData(val event: InterModProcessEvent) : IModuleEventData
