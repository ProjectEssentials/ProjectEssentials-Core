package com.mairwunnx.projectessentials.core.api.v1.providers

import com.mairwunnx.projectessentials.core.api.v1.events.EmptyEventData
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.OnProvidersCreating
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.OnProvidersInitializing
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ProviderEventData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import java.io.File

private var initialized = false
private val logger = LogManager.getLogger()
private val marker = MarkerManager.Log4jMarker("PROVIDER")

/**
 * Provider directory.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
val providerDirectory by lazy {
    return@lazy File(".").absolutePath
        .plus(File.separator)
        .plus("config")
        .plus(File.separator)
        .plus("ProjectEssentials")
        .plus(File.separator)
        .plus("providers")
}

/**
 * Initialize providers, call it in core module
 * initialize block. Before any events.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun initializeProviders() {
    if (!initialized) {
        initialized = true

        ModuleEventAPI.fire(OnProvidersInitializing, EmptyEventData())

        logger.info(marker, "Initializing providers, creating directories")
        File(providerDirectory).mkdirs()
    }
}

/**
 * Creates new provider with specific name.
 * @param provider provider name.
 * @return File class instance.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
fun createProvider(provider: String): File {
    ModuleEventAPI.fire(OnProvidersCreating, ProviderEventData(provider))

    logger.info(marker, "Creating provider file for $provider provider")

    val file = File(
        providerDirectory + File.separator + "$provider.provider"
    )
    file.createNewFile()
    return file
}


