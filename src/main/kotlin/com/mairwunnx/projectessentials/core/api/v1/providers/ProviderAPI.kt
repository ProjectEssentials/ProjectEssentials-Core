package com.mairwunnx.projectessentials.core.api.v1.providers

import com.mairwunnx.projectessentials.core.api.v1.commands.Command
import com.mairwunnx.projectessentials.core.api.v1.configuration.Configuration
import com.mairwunnx.projectessentials.core.api.v1.module.Module
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation

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
    private val providers = mutableMapOf<ProviderType, MutableList<KClass<*>>>()

    /**
     * Adds target provider. (provider type will determine automatically)
     *
     * **Call only in module initialize block or constructor!!**
     * @param kclazz provider class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    @Synchronized
    @OptIn(ExperimentalStdlibApi::class)
    fun addProvider(kclazz: KClass<*>) = when {
        kclazz.hasAnnotation<Configuration>() -> {
            addProvider(ProviderType.CONFIGURATION, kclazz).run {
                logger.debug(
                    marker,
                    "Provider class founded: Type: `Configuration`, Class: `${kclazz.simpleName}`"
                )
            }
        }
        kclazz.hasAnnotation<Module>() -> {
            addProvider(ProviderType.MODULE, kclazz).run {
                logger.debug(
                    marker,
                    "Provider class founded: Type: `Module`, Class: `${kclazz.simpleName}`"
                )
            }
        }
        kclazz.hasAnnotation<Command>() -> {
            addProvider(ProviderType.COMMAND, kclazz).run {
                logger.debug(
                    marker,
                    "Provider class founded: Type: `Command`, Class: `${kclazz.simpleName}`"
                )
            }
        }
        else -> logger.warn(
            marker,
            "Incorrect provider class found! (skipped to load): Class: `${kclazz.simpleName}`"
        )
    }

    /**
     * Adds target provider with specified type.
     *
     * **Call only in module initialize block or constructor!!**
     * @param type provider type.
     * @param kclazz provider class.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addProvider(type: ProviderType, kclazz: KClass<*>) {
        providers[type]?.add(kclazz) ?: providers.put(type, mutableListOf(kclazz))
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
