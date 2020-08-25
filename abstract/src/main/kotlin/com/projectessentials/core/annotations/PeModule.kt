package com.projectessentials.core.annotations

/**
 * Annotate with this annotation project essentials
 * module, that annotation will alive only at compile
 * time.
 *
 * @since 3.0.0.
 * @property priority loading priority of module. By
 * default is `0`.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class PeModule(val priority: Int = 0)
