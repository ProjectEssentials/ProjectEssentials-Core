package com.mairwunnx.projectessentials.core.api.v1.providers

import com.mairwunnx.projectessentials.core.api.v1.commands.ICommand
import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

/**
 * Provider API class. If you build new module
 * for project essentials then you must use this class.
 *
 * You can register providers (configurations, commands and module).
 *
 * You can register manually (just addProvider(<provider class>)) and you can
 * use ClassIndex library, code example (kotlin language) (it scans all
 * configuration classes with annotations):
 *
 * ```kotlin
 * ClassIndex.getAnnotated(Configuration::class.java).forEach {
 *   ProviderAPI.addProvider(it)
 * }
 * ```
 *
 * BTW! `addProvider(...)` you must call only in init block of your mod.
 * (or if you use java as language then in constructor)
 *
 * ["ClassIndex library"](https://github.com/atteo/classindex)
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
object ProviderAPI {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("PROVIDER")
    private val providers = mutableMapOf<ProviderType, MutableList<Class<*>>>()

    /**
     * Adds target provider. (provider type will determine automatically)
     *
     * **Call only in module initialize block or constructor!!**
     * @param clazz provider class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    @Synchronized
    fun addProvider(clazz: Class<*>) = when (clazz) {
        is IConfiguration<*> -> {
            addProvider(ProviderType.CONFIGURATION, clazz).run {
                logger.debug(
                    marker,
                    "Provider class founded: Type: `Configuration`, Class: `${clazz.simpleName}`"
                )
            }
        }
        is IModule -> {
            addProvider(ProviderType.MODULE, clazz).run {
                logger.debug(
                    marker,
                    "Provider class founded: Type: `Module`, Class: `${clazz.simpleName}`"
                )
            }
        }
        is ICommand -> {
            addProvider(ProviderType.COMMAND, clazz).run {
                logger.debug(
                    marker,
                    "Provider class founded: Type: `Command`, Class: `${clazz.simpleName}`"
                )
            }
        }
        else -> logger.warn(
            marker,
            "Incorrect provider class found! (skipped to load): Class: `${clazz.simpleName}`"
        )
    }

    /**
     * Adds target provider with specified type.
     *
     * **Call only in module initialize block or constructor!!**
     * @param type provider type.
     * @param clazz provider class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addProvider(type: ProviderType, clazz: Class<*>) {
        providers[type]?.add(clazz) ?: providers.put(type, mutableListOf(clazz))
    }

    /**
     * @return all added providers.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getAllProviders() = providers

    /**
     * @param type provider type to return.
     * @return all specified for type providers.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getProvidersByType(type: ProviderType) = getAllProviders()[type] ?: mutableListOf()
}
