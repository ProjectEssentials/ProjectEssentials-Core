package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor

/**
 * Provider event data, stores processor instance.
 * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
 */
class ProcessorEventData(val processor: IProcessor) : IModuleEventData
