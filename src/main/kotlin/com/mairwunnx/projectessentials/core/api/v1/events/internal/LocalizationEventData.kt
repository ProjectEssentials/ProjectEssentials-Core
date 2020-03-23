package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization

/**
 * Localization event data, stores localization instance.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class LocalizationEventData(val localization: Localization) : IModuleEventData
