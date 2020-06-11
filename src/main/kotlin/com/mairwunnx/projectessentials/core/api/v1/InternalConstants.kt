@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS", "unused")

package com.mairwunnx.projectessentials.core.api.v1

/**
 * Configuration processor loading index order.
 * @since 2.0.0-SNAPSHOT.1.
 */
const val CONFIGURATION_PROCESSOR_INDEX = 0

/**
 * Module processor loading index order.
 * @since 2.0.0-SNAPSHOT.1.
 */
const val MODULE_PROCESSOR_INDEX = 1

/**
 * Localization processor loading index order.
 * @since 2.0.0-SNAPSHOT.1.
 */
const val LOCALIZATION_PROCESSOR_INDEX = 2

/**
 * Command processor loading index order.
 * @since 2.0.0-SNAPSHOT.1.
 */
const val COMMAND_PROCESSOR_INDEX = 3

/**
 * Initial fall back language, uses before
 * configuration loading.
 * @since 2.0.0-SNAPSHOT.1.
 */
const val INITIAL_FALLBACK_LANGUAGE = "en_us"

/**
 * Prefix for localized messages for core module.
 * @since 2.0.0-SNAPSHOT.1.
 */
const val MESSAGE_CORE_PREFIX = "project_essentials_core"

/**
 * Prefix for localized messages for other modules.
 * @since 2.0.0-SNAPSHOT.1.
 */
const val MESSAGE_MODULE_PREFIX = "project_essentials_"

const val SETTING_LOC_ENABLED = "v2-localization-enabled"
const val SETTING_LOC_FALLBACK_LANG = "v2-localization-fallback-lang"
const val SETTING_DISABLE_SAFE_ENCHANT = "disable-safely-enchant-level"
const val SETTING_NATIVE_COMMAND_REPLACE = "enable-native-command-replace"
const val SETTING_NATIVE_COMMAND_REQUIRE_PREDICATE_REPLACE =
    "enable-native-command-require-predicate-replace"
const val SETTING_NATIVE_COMMAND_ALIASES_REPLACE = "enable-native-command-aliases-replace"
const val SETTING_LOCATE_COMMAND_FIND_RADIUS = "locate-command-find-radius"
const val SETTING_DISABLE_PORTAL_SPAWNING = "disable-portal-spawning"
const val SETTING_DEOP_COMMAND_REMOVE_OP_PERM = "deop-command-remove-op-perm"
const val SETTING_LIST_MAX_ELEMENTS_IN_PAGE = "list-max-elements-in-page"
const val SETTING_ENABLE_CONSOLE_COLORS = "enable-console-colors"

const val IMCLocalizationMessage = "localization-request"
const val IMCProvidersMessage = "providers-request"
