@file:Suppress("NOTHING_TO_INLINE")

package com.projectessentials.core

/**
 * Return the version of this implementation. It consists of
 * any string assigned by the vendor of this implementation
 * and does not have any particular syntax specified or expected
 * by the Java runtime. It may be compared for equality with
 * other package version strings used for this implementation
 * by this vendor for this package.
 *
 * @since 3.0.0
 * @receiver Class<*> any java class as receiver.
 * @return implementation version, represented as single integer,
 * if it's null returns `-1`.
 */
inline fun <reified T> T.implVersion() =
    this!!::class.java.`package`.implementationVersion?.toIntOrNull() ?: -1

/**
 * Return the title of this package.
 *
 * @since 3.0.0
 * @receiver Class<*> any java class as receiver.
 * @return implementation name, represented as string, if it's
 * null returns `unknown`.
 */
inline fun <reified T> T.implName() = this!!::class.java.`package`.implementationTitle ?: "unknown"
