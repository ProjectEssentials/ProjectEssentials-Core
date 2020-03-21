package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData

/**
 * Configuration event data, stores configuration instance.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class ConfigurationEventData(val configuration: IConfiguration<*>) : IModuleEventData
