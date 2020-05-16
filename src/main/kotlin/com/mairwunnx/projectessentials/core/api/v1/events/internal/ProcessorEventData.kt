package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor

/**
 * Provider event data, stores processor instance.
 * @since 2.0.0-SNAPSHOT.1.
 */
class ProcessorEventData(val processor: IProcessor) : IModuleEventData
