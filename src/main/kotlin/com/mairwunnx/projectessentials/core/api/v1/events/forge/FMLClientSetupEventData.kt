package com.mairwunnx.projectessentials.core.api.v1.events.forge

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

/**
 * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
 */
class FMLClientSetupEventData(val event: FMLClientSetupEvent) : IModuleEventData
