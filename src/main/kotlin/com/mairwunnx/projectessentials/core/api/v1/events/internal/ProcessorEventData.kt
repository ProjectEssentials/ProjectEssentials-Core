package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor

/**
 * Provider event data, stores processor instance.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class ProcessorEventData(val processor: IProcessor) : IModuleEventData
