package com.mairwunnx.projectessentials.core.api.v1.localization

@Deprecated("Deprecated feature, but still existing for backward compatibility.")
data class Localization(
    val sources: MutableList<String>, val sourceName: String, val sourceClass: Class<*>
)
