@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.configuration

import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ConfigurationEventData
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderType
import org.apache.logging.log4j.LogManager

/**
 * Configuration API, for interacting with configurations.
 * @since 2.0.0-SNAPSHOT.1.
 */
object ConfigurationAPI {
    private val logger = LogManager.getLogger()
    private var configurations = listOf<IConfiguration<*>>()

    /**
     * @return all installed and checked configurations.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getConfigurations() = configurations

    /**
     * @param name processor name.
     * @throws ConfigurationNotFoundException
     * @return configuration by name. If configuration with
     * name not exist then throws `ConfigurationNotFoundException`.
     * @since 2.0.0-SNAPSHOT.1.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getConfigurationByName(name: String): T where T : IConfiguration<*> =
        configurations.asSequence().find {
            it.name.toLowerCase() == name.toLowerCase()
        }?.let {
            return@let it as T
        } ?: throw ConfigurationNotFoundException("Configuration with name $name not found.")

    /**
     * Reloads all initialized and processed configurations.
     * @param saveBeforeLoad if value is true then configuration
     * will be saved before loading. Default value is `true`.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun reloadAll(saveBeforeLoad: Boolean = true) {
        getConfigurations().forEach { cfg ->
            if (saveBeforeLoad) cfg.save().also { cfg.load() }
        }
    }

    /**
     * Reloads specified configuration.
     * @param configuration configuration for reloading.
     * @param saveBeforeLoad if value is true then configuration
     * will be saved before loading. Default value is `true`.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun reloadSpecified(
        configuration: IConfiguration<*>,
        saveBeforeLoad: Boolean = true
    ) {
        if (saveBeforeLoad) configuration.save().also { configuration.load() }
    }

    /**
     * Saves all initialized and processed configurations.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun saveAll() = getConfigurations().forEach { it.save() }

    internal fun loadAll() {
        ProviderAPI.getProvidersByType(ProviderType.Configuration).forEach {
            val clazz = it.getDeclaredField("INSTANCE").get(null) as IConfiguration<*>
            ModuleEventAPI.fire(
                ModuleCoreEventType.OnConfigurationClassProcessing, ConfigurationEventData(clazz)
            )
            logger.debug(
                "Configuration taken! ${it.simpleName}, name: ${clazz.name}, version: ${clazz.version}, at ${clazz.path}"
            )
            configurations = configurations + clazz
            load(clazz)
            ModuleEventAPI.fire(
                ModuleCoreEventType.OnConfigurationClassProcessed, ConfigurationEventData(clazz)
            )
        }
    }

    private fun load(configuration: IConfiguration<*>) =
        logger.info("Starting loading configuration ${configuration.name}").also {
            configuration.load()
        }
}
