@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")

package com.mairwunnx.projectessentials.core.api.v1

/**
 * Configuration processor loading index order.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
const val CONFIGURATION_PROCESSOR_INDEX = 0u

/**
 * Module processor loading index order.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
const val MODULE_PROCESSOR_INDEX = 1u

/**
 * Localization processor loading index order.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
const val LOCALIZATION_PROCESSOR_INDEX = 2u

/**
 * Command processor loading index order.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
const val COMMAND_PROCESSOR_INDEX = 3u

/**
 * Initial fall back language, uses before
 * configuration loading.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
const val INITIAL_FALLBACK_LANGUAGE = "en_us"
