package com.mairwunnx.projectessentials.core.api.v1.events

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when module event not found.
 * @param message exception message.
 * @since 2.0.0-SNAPSHOT.1.
 */
class EventNotFoundException(message: String = String.empty) : Exception(message)
