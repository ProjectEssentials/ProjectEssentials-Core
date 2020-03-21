package com.mairwunnx.projectessentials.core.api.v1.events

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Throws when module event not found.
 * @param message exception message.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class EventNotFoundException(
    message: String = String.empty
) : Exception(message)
