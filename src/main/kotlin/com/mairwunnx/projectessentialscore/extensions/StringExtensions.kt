@file:Suppress("unused")

package com.mairwunnx.projectessentialscore.extensions

/**
 * Capitalize each word in string.
 * @return capitalized each word string.
 * @since 1.14.4-1.0.0.0
 */
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.capitalize() }

/**
 * Return empty string.
 * @since 1.14.4-1.0.0.0
 */
val String.Companion.empty get() = ""
