package com.mairwunnx.projectessentials.core.api.v1.configuration

import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.createProvider
import org.reflections.Reflections

@OptIn(ExperimentalUnsignedTypes::class)
internal object ConfigurationProcessor : IProcessor {
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
        loadDomains()
    }

    private fun loadDomains() {
        provider.readLines().forEach {
            allowedDomains.add(it)
        }
    }

    override fun process() {
        allowedDomains.forEach { domain ->
            val reflections = Reflections(domain)
            reflections.getTypesAnnotatedWith(
                Configuration::class.java
            ).forEach { configurationClass ->
                if (isConfiguration(configurationClass)) {
                    configurationClass as IConfiguration<*>
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
            it.load()
        }
    }
}
