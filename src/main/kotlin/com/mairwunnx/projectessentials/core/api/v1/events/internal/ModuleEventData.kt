package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.module.IModule

/**
 * Domain event data, stores domain name (package path).
 * @since 2.0.0-SNAPSHOT.1.
 */
class ModuleEventData(val module: IModule) : IModuleEventData
