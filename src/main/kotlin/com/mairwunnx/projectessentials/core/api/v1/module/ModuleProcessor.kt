package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.DomainEventData
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.createProvider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import org.reflections.Reflections

@OptIn(ExperimentalUnsignedTypes::class)
internal object ModuleProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("MODULE PROCESSOR")
    private val provider = createProvider("module")
    private var modules = listOf<IModule>()
    private val interfaceName = IModule::class.java.name
    private val allowedDomains = mutableListOf(
        "com.mairwunnx"
    )

    fun getModules() = modules
    fun getAllowedDomains() = allowedDomains

    override val processorLoadIndex = 1u
    override val processorName = "module"

    override fun initialize() {
        logger.info(marker, "Initializing module processor")
        logger.info(marker, "Loading allowed package domains")
        loadDomains()
    }

    private fun loadDomains() {
        provider.readLines().forEach {
            ModuleEventAPI.fire(OnAllowedDomainLoading, DomainEventData(it))
            logger.info(marker, "Loaded module domain: $it")
            allowedDomains.add(it)
        }
    }

    override fun process() {
        logger.info(marker, "Finding and processing modules")

        allowedDomains.forEach { domain ->
            ModuleEventAPI.fire(OnAllowedDomainProcessing, DomainEventData(domain))

            val reflections = Reflections(domain)
            reflections.getTypesAnnotatedWith(
                Module::class.java
            ).forEach { moduleClass ->
                if (isModule(moduleClass)) {
                    moduleClass as IModule

                    ModuleEventAPI.fire(OnModuleClassProcessing, ModuleEventData(moduleClass))

                    processIndexes(
                        moduleClass.getModuleData().loadIndex
                    )

                    logger.info(
                        marker,
                        "\n    *** Module found! ${moduleClass.name}".plus(
                            "\n\n  - Name: ${moduleClass.getModuleData().name}"
                        ).plus(
                            "\n  - Version: ${moduleClass.getModuleData().version}"
                        ).plus(
                            "\n  - API Version: ${moduleClass.getModuleData().apiVersion}"
                        )
                    )
                    modules = modules + moduleClass

                    ModuleEventAPI.fire(OnModuleClassProcessed, ModuleEventData(moduleClass))
                }
            }
        }
        sortByLoadIndex()
    }

    private fun processIndexes(index: UInt) {
        modules.forEach {
            if (it.getModuleData().loadIndex == index) {
                throw ModuleIndexDuplicateException(
                    "Module with same load index $index already processed."
                )
            }
        }
    }

    private fun isModule(clazz: Class<*>): Boolean {
        val interfaces = clazz.interfaces
        interfaces.forEach {
            if (it.name == interfaceName) {
                return true
            }
        }
        return false
    }

    private fun sortByLoadIndex() {
        modules = modules.sortedWith(compareBy {
            it.getModuleData().loadIndex
        })
    }

    override fun postProcess() {
        getModules().forEach {
            ModuleEventAPI.fire(OnModuleClassPostProcessing, ModuleEventData(it))
            logger.info(marker, "Starting initializing module ${it.getModuleData().name}")
            it.init()
        }
    }
}
