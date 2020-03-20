package com.mairwunnx.projectessentials.core.api.v1.configuration

import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.createProvider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import org.reflections.Reflections

@OptIn(ExperimentalUnsignedTypes::class)
internal object ConfigurationProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("CONFIGURATION PROCESSOR")
    private val provider = createProvider("configuration")
    private var configurations = listOf<IConfiguration<*>>()
    private val interfaceName = IConfiguration::class.java.name
    private val allowedDomains = mutableListOf(
        "com.mairwunnx"
    )

    fun getConfigurations() = configurations
    fun getAllowedDomains() = allowedDomains

    override val processorLoadIndex = 0u
    override val processorName = "configuration"

    override fun initialize() {
        logger.info(marker, "Initializing configuration processor")
        logger.info(marker, "Loading allowed package domains")
        loadDomains()
    }

    private fun loadDomains() {
        provider.readLines().forEach {
            logger.info(marker, "Loaded configuration domain: $it")
            allowedDomains.add(it)
        }
    }

    override fun process() {
        logger.info(marker, "Finding and processing configurations")

        allowedDomains.forEach { domain ->
            val reflections = Reflections(domain)
            reflections.getTypesAnnotatedWith(
                Configuration::class.java
            ).forEach { configurationClass ->
                if (isConfiguration(configurationClass)) {
                    configurationClass as IConfiguration<*>
                    logger.info(
                        marker,
                        "\n    *** Configuration taken! ${configurationClass.name}".plus(
                            "\n  - Name: ${configurationClass.getConfigurationData().name}"
                        ).plus(
                            "\n  - Path: ${configurationClass.path}"
                        )
                    )
                    configurations = configurations + configurationClass
                }
            }
        }
    }

    private fun isConfiguration(clazz: Class<*>): Boolean {
        val interfaces = clazz.interfaces
        interfaces.forEach {
            if (it.name == interfaceName) {
                return true
            }
        }
        return false
    }

    override fun postProcess() {
        getConfigurations().forEach {
            logger.info(
                marker, "Starting loading configuration ${it.getConfigurationData().name}"
            )
            it.load()
        }
    }
}
