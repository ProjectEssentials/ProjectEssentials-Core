package com.mairwunnx.projectessentials.core.api.v1.processor

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Throws when processor not found.
 * @param message exception message.
 */
class ProcessorNotFoundException(
    message: String = String.empty
) : Exception(message)
