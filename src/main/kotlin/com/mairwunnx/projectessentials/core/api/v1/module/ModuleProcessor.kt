package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.createProvider
import org.reflections.Reflections

@OptIn(ExperimentalUnsignedTypes::class)
internal object ModuleProcessor : IProcessor {
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
                Module::class.java
            ).forEach { moduleClass ->
                if (isModule(moduleClass)) {
                    moduleClass as IModule
                    processIndexes(
                        moduleClass.getModuleData().loadIndex
                    )
                    modules = modules + moduleClass
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
            it.init()
        }
    }
}
