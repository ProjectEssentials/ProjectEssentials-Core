package com.projectessentials.core

/**
 * Implementation contract marks the object
 * is reloadable, and contains one [reload]
 * method.
 *
 * @since 3.0.0
 */
interface Reloadable {
    /**
     * Performs reload action for this object.
     *
     * @since 3.0.0
     */
    fun reload()
}
