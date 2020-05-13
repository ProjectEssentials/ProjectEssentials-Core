package com.mairwunnx.projectessentials.core.api.v1.localization

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when localization not found.
 * @param message exception message.
 * @since 2.0.0-SNAPSHOT.1.
 */
class LocalizationNotFoundException(
    message: String = String.empty
) : Exception(message)
