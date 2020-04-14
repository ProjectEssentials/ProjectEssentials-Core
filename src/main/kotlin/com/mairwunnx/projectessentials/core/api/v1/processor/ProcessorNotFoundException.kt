package com.mairwunnx.projectessentials.core.api.v1.processor

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when processor not found.
 * @param message exception message.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
class ProcessorNotFoundException(
    message: String = String.empty
) : Exception(message)
