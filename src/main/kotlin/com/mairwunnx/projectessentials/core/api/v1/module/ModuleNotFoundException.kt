package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Throws when module not found.
 * @param message exception message.
 */
class ModuleNotFoundException(
    message: String = String.empty
) : Exception(message)
