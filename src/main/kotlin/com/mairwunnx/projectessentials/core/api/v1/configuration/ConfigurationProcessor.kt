package com.mairwunnx.projectessentials.core.api.v1.configuration

import com.mairwunnx.projectessentials.core.api.v1.CONFIGURATION_PROCESSOR_INDEX
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ConfigurationEventData
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

internal object ConfigurationProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("CONFIGURATION PROCESSOR")
    private var configurations = listOf<IConfiguration<*>>()

    fun getConfigurations() = configurations

    override val processorLoadIndex = CONFIGURATION_PROCESSOR_INDEX
    override val processorName = "configuration"

    override fun initialize() = Unit

    override fun process() {
        logger.debug(marker, "Finding and processing configurations")

        ProviderAPI.getProvidersByType(ProviderType.CONFIGURATION).forEach {
            if (isConfiguration(it)) {
                val clazz = it.getDeclaredField("INSTANCE").get(null) as IConfiguration<*>

                ModuleEventAPI.fire(OnConfigurationClassProcessing, ConfigurationEventData(clazz))

                logger.debug(
                    marker,
                    """

    ### Configuration taken! ${it.simpleName}
        - Name: ${clazz.name}
        - Version: ${clazz.version}
        - Class: ${it.canonicalName}
        - Path: ${clazz.path}

                    """
                )
                configurations = configurations + clazz
                ModuleEventAPI.fire(OnConfigurationClassProcessed, ConfigurationEventData(clazz))
            }
        }
    }

    private fun isConfiguration(clazz: Class<*>) =
        IConfiguration::class.java.isAssignableFrom(clazz)

    override fun postProcess() {
        getConfigurations().forEach {
            ModuleEventAPI.fire(OnConfigurationClassPostProcessing, ConfigurationEventData(it))
            logger.info(marker, "Starting loading configuration ${it.name}")
            it.load()
        }
    }
}
