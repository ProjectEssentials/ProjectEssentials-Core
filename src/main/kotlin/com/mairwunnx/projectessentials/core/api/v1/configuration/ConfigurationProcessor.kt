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
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@OptIn(ExperimentalUnsignedTypes::class)
internal object ConfigurationProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("CONFIGURATION PROCESSOR")
    private var configurations = listOf<IConfiguration<*>>()

    fun getConfigurations() = configurations

    override val processorLoadIndex = CONFIGURATION_PROCESSOR_INDEX
    override val processorName = "configuration"

    override fun initialize() {
        logger.info(marker, "Initializing configuration processor")
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun process() {
        logger.info(marker, "Finding and processing configurations")

        ProviderAPI.getProvidersByType(ProviderType.CONFIGURATION).forEach {
            if (isConfiguration(it)) {
                val clazz = it.objectInstance as IConfiguration<*>

                ModuleEventAPI.fire(
                    OnConfigurationClassProcessing, ConfigurationEventData(clazz)
                )

                logger.info(
                    marker,
                    "\n\n    *** Configuration taken! ${it.simpleName}".plus(
                        "\n\n  - Name: ${clazz.data().name}"
                    ).plus(
                        "\n  - Version: ${clazz.data().version}"
                    ).plus(
                        "\n  - Class: ${it.qualifiedName}"
                    ).plus(
                        "\n  - Path: ${clazz.path}\n\n"
                    )
                )
                configurations = configurations + clazz

                ModuleEventAPI.fire(
                    OnConfigurationClassProcessed,
                    ConfigurationEventData(clazz)
                )
            }
        }
    }

    private fun isConfiguration(kclazz: KClass<*>) =
        kclazz.isSubclassOf(IConfiguration::class)

    override fun postProcess() {
        getConfigurations().forEach {
            ModuleEventAPI.fire(
                OnConfigurationClassPostProcessing, ConfigurationEventData(it)
            )

            logger.info(
                marker, "Starting loading configuration ${it.data().name}"
            )
            it.load()
        }
    }
}
