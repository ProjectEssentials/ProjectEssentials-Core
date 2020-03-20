package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Configuration API, for interacting with configurations.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
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

    /**
     * @param name processor name.
     * @throws ConfigurationNotFoundException
     * @return configuration by name. If configuration with
     * name not exist then throws `ConfigurationNotFoundException`.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getConfigurationByName(name: String): IConfiguration<*> {
        getAllConfigurations().forEach {
            if (it.getConfigurationData().name == name) {
                return it
            }
        }
        throw ConfigurationNotFoundException(
            "Configuration with name $name not found."
        )
    }
}
