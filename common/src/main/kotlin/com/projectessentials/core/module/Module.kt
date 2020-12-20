package com.projectessentials.core.module

import com.projectessentials.core.Reloadable

/**
 * Implementation contract of Project Essentials
 * modules.
 *
 * @property id id of the module separated by underscores.
 * @property priority priority of module for loading.
 * @property version version of the module represented as
 * incremental integer number.
 * @since 3.0.0
 */
interface Module : Reloadable {
    val id: String
    val priority: Int
    val version: Int

    /**
     * Will called when module will ready to use
     * other objects of mod loader platform and
     * other configurations, this will called asynchronously.
     *
     * @since 3.0.0
     */
    suspend fun initialize()
}
