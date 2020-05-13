package com.mairwunnx.projectessentials.core.api.v1.processor

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when processor not found.
 * @param message exception message.
 * @since 2.0.0-SNAPSHOT.1.
 */
class ProcessorNotFoundException(message: String = String.empty) : Exception(message)
