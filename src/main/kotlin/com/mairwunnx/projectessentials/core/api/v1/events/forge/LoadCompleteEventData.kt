package com.mairwunnx.projectessentials.core.api.v1.events.forge

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent

/**
 * @since 2.0.1.
 */
class LoadCompleteEventData(val event: FMLLoadCompleteEvent) : IModuleEventData
