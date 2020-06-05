@file:Suppress("MemberVisibilityCanBePrivate")

package com.mairwunnx.projectessentials.core.api.v1.providers

import com.mairwunnx.projectessentials.core.api.v1.commands.ICommand
import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.api.v1.providersMarker
import org.apache.logging.log4j.LogManager
import java.util.*
import kotlin.collections.HashMap


/**
 * Provider API class. If you build new module
 * for project essentials then you must use this class.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
object ProviderAPI {
    private val logger = LogManager.getLogger()
    private val providers = Collections.synchronizedMap(
        HashMap<ProviderType, MutableList<Class<*>>>()
    )

    /**
     * Adds target provider. (provider type will determine automatically)
     *
     * @param clazz provider class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addProvider(clazz: Class<*>) {
        fun out(type: String) = logger.debug(
            providersMarker, "Provider class found: Type: `$type`, Class: `${clazz.simpleName}`"
        )

        when {
            IConfiguration::class.java.isAssignableFrom(clazz) -> {
                addProvider(ProviderType.Configuration, clazz).run { out("Configuration") }
            }
            IModule::class.java.isAssignableFrom(clazz) -> {
                addProvider(ProviderType.Module, clazz).run { out("Module") }
            }
            ICommand::class.java.isAssignableFrom(clazz) -> {
                addProvider(ProviderType.Command, clazz).run { out("Command") }
            }
            else -> logger.warn(
                providersMarker,
                "Incorrect provider class found! (skipped to load): Class: `${clazz.simpleName}`"
            )
        }
    }

    /**
     * Adds target provider with specified type.
     *
     * @param type provider type.
     * @param clazz provider class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addProvider(type: ProviderType, clazz: Class<*>) {
        synchronized(providers) {
            providers[type]?.add(clazz) ?: providers.put(type, mutableListOf(clazz))
        }
    }

    /**
     * @return all added providers.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getProviders() = providers

    /**
     * @param type provider type to return.
     * @return all specified for type providers.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getProvidersByType(type: ProviderType) = getProviders()[type] ?: mutableListOf()
}
