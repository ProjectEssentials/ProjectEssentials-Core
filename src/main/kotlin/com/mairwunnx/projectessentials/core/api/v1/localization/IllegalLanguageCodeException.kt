package com.mairwunnx.projectessentials.core.api.v1.localization

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when localization code has incorrect format.
 * @param message exception message.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
class IllegalLanguageCodeException(
    message: String = String.empty
) : Exception(message)
