package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.api.v1.MODULE_PROCESSOR_INDEX
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderType
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

internal object ModuleProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("MODULE PROCESSOR")
    private var modules = listOf<IModule>()

    fun getModules() = modules

    override val processorLoadIndex = MODULE_PROCESSOR_INDEX
    override val processorName = "module"

    override fun initialize() = Unit

    override fun process() {
        logger.debug(marker, "Finding and processing modules")

        ProviderAPI.getProvidersByType(ProviderType.MODULE).forEach {
            if (isModule(it)) {
                val clazz = if (isForgeInstanceProvider(it)) {
                    // For other modules whose instances created by forge.
                    getForgeProvidedInstance(it)
                } else {
                    // For core basically. Because core also load modules and other stuff.
                    it.newInstance() as IModule
                }

                ModuleEventAPI.fire(OnModuleClassProcessing, ModuleEventData(clazz))
                processIndexes(clazz.loadIndex)

                logger.debug(
                    marker,
                    """

    ### Project Essentials module found: ${it.simpleName}
        - Name: ${clazz.name}
        - Class: ${it.canonicalName}
        - Version: ${clazz.version}

                    """
                )
                modules = modules + clazz
                ModuleEventAPI.fire(OnModuleClassProcessed, ModuleEventData(clazz))
            }
        }
        sortByLoadIndex()
    }

    private fun processIndexes(index: Int) {
        modules.find { it.loadIndex == index }?.let {
            throw ModuleIndexDuplicateException(
                "Module with same load index $index already processed."
            )
        }
    }

    private fun isModule(clazz: Class<*>) = clazz is IModule

    private fun getInstanceModId(clazz: Class<*>) =
        clazz.getAnnotation(Mod::class.java)?.value ?: String.empty

    private fun isForgeInstanceProvider(clazz: Class<*>) =
        clazz.isAnnotationPresent(Mod::class.java)

    private fun getForgeProvidedInstance(clazz: Class<*>) =
        ModList.get().getModObjectById<IModule>(getInstanceModId(clazz)).get()

    private fun sortByLoadIndex() {
        modules = modules.sortedWith(compareBy { it.loadIndex })
    }

    override fun postProcess() {
        getModules().forEach {
            ModuleEventAPI.fire(OnModuleClassPostProcessing, ModuleEventData(it))
            logger.info(marker, "Starting initializing module ${it.name}")
            it.init()
        }
    }
}
