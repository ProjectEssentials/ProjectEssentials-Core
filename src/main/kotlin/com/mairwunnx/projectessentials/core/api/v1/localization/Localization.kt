package com.mairwunnx.projectessentials.core.api.v1.localization

data class Localization(
    val sources: MutableList<String>, val sourceName: String, val sourceClass: Class<*>
)
