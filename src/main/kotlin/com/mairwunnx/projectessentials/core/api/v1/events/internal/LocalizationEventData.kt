package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization

/**
 * Localization event data, stores localization instance.
 * @since 2.0.0-SNAPSHOT.1.
 */
class LocalizationEventData(val localization: Localization) : IModuleEventData
