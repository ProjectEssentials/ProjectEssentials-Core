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

/**
 * Prefix for localized messages for core module.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
const val MESSAGE_CORE_PREFIX = "project_essentials_core"

const val SETTING_LOC_ENABLED = "v2-localization-enabled"
const val SETTING_LOC_FALLBACK_LANG = "v2-localization-fallback-lang"
const val SETTING_DISABLE_SAFE_ENCHANT = "disable-safely-enchant-level"
const val SETTING_NATIVE_COMMAND_REPLACE = "enable-native-command-replace"
const val SETTING_LOCATE_COMMAND_FIND_RADIUS = "locate-command-find-radius"
const val SETTING_DISABLE_PORTAL_SPAWNING = "disable-portal-spawning"
