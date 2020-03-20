package com.mairwunnx.projectessentials.core.api.v1.configuration

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Throws when configuration not found.
 * @param message exception message.
 */
class ConfigurationNotFoundException(
    message: String = String.empty
) : Exception(message)
