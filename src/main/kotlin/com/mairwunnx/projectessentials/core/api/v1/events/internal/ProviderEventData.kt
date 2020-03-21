package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData

/**
 * Provider event data, stores provider name.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class ProviderEventData(val providerName: String) : IModuleEventData
