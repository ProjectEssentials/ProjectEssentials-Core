package com.mairwunnx.projectessentials.core.api.v1.processor

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Throws when processor with same load index already exist.
 * @param message exception message.
 */
class ProcessorIndexDuplicateException(
    message: String = String.empty
) : Exception(message)
