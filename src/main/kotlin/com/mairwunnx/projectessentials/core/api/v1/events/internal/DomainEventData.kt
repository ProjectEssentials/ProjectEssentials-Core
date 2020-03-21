package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData

/**
 * Domain event data, stores domain name (package path).
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class DomainEventData(val domainName: String) : IModuleEventData
