package com.mairwunnx.projectessentials.core.configuration.localization

import kotlinx.serialization.Serializable

@Serializable
data class LocalizationConfiguration(
    val enabled: Boolean = false,
    val fallbackLanguage: String = "en_us"
)
