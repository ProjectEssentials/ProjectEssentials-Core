package com.mairwunnx.projectessentials.core.api.v1.configuration

@Suppress("unused")
object ConfigurationAPI {
    /**
     * @return all installed and checked configurations.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllConfigurations() = ConfigurationProcessor.getConfigurations()

    /**
     * @return allowed domains to indexing configuration classes.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllowedDomains() = ConfigurationProcessor.getAllowedDomains()
}
