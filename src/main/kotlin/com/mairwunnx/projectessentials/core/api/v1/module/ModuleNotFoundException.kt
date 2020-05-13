package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when module not found.
 * @param message exception message.
 * @since 2.0.0-SNAPSHOT.1.
 */
class ModuleNotFoundException(
    message: String = String.empty
) : Exception(message)
