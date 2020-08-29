package com.projectessentials.core.annotations

/**
 * Annotate with this annotation project essentials
 * configuration classes, that annotation will alive
 * only at compile time.
 *
 * Need for collect configurations from module.
 *
 * @since 3.0.0.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ProvideConfiguration