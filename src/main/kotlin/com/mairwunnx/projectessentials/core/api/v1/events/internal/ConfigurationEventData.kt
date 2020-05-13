package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData

/**
 * Configuration event data, stores configuration instance.
 * @since 2.0.0-SNAPSHOT.1.
 */
class ConfigurationEventData(
    val configuration: IConfiguration<*>
) : IModuleEventData
