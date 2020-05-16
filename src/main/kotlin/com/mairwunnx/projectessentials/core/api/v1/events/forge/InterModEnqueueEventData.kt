package com.mairwunnx.projectessentials.core.api.v1.events.forge

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent

/**
 * @since 2.0.0-SNAPSHOT.1.
 */
class InterModEnqueueEventData(val event: InterModEnqueueEvent) : IModuleEventData
