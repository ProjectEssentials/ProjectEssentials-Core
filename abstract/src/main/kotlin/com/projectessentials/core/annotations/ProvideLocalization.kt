package com.projectessentials.core.annotations

/**
 * Annotate with this annotation project essentials
 * module, that annotation will alive only at compile
 * time.
 *
 * Need for collect localizations from module.
 *
 * @since 3.0.0.
 * @property id name of module wanted to process localization.
 * @property langs languages what can used in module.
 */
annotation class ProvideLocalization(val id: String, val langs: Array<String>)
