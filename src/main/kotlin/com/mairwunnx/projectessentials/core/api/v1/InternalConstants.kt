@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")

package com.mairwunnx.projectessentials.core.api.v1

/**
 * Configuration processor loading index order.
 */
const val CONFIGURATION_PROCESSOR_INDEX = 0u

/**
 * Module processor loading index order.
 */
const val MODULE_PROCESSOR_INDEX = 1u

/**
 * Localization processor loading index order.
 */
const val LOCALIZATION_PROCESSOR_INDEX = 2u

/**
 * Initial fall back language, uses before
 * configuration loading.
 */
const val INITIAL_FALLBACK_LANGUAGE = "en_us"
