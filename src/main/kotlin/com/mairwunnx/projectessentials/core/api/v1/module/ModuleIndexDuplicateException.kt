package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Throws when module with same load index already exist.
 * @param message exception message.
 */
class ModuleIndexDuplicateException(
    message: String = String.empty
) : Exception(message)
