package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderType
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

/**
 * Class for interacting with other modules.
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ModuleAPI {
    private val logger = LogManager.getLogger()
    private var modules = listOf<IModule>()

    /**
     * @return all installed and checked modules.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getModules() = modules

    /**
     * @return module mod id what declared in `@Mod` annotation.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getModuleModId(module: IModule): String {
        if (module.javaClass.isAnnotationPresent(Mod::class.java)) {
            return module.javaClass.getAnnotation(Mod::class.java).value
        }
        return "project_essentials_${module.name.toLowerCase()}"
    }

    /**
     * @return module by provided name.
     * @throws ModuleNotFoundException when module not found.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getModuleByName(name: String) =
        getModules().asSequence().find { it.name.toLowerCase() == name.toLowerCase() }?.let {
            return@let it
        } ?: throw ModuleNotFoundException(
            "Module with name $name not found and not processed."
        )

    /**
     * @param module module name what provided in Module annotation.
     * @return true if module existing or installed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun isModuleExist(module: String) = getModules().asSequence().find {
        it.name.toLowerCase() == module.toLowerCase()
    }.let { return@let it != null }

    internal fun initializeOrdered() {
        ProviderAPI.getProvidersByType(ProviderType.Module).forEach {
            val clazz = if (it.isAnnotationPresent(Mod::class.java)) {
                ModList.get().getModObjectById<IModule>(
                    it.getAnnotation(Mod::class.java)?.value ?: String.empty
                ).get()
            } else it.newInstance() as IModule
            ModuleEventAPI.fire(ModuleCoreEventType.OnModuleClassProcessing, ModuleEventData(clazz))
            processIndexes(clazz.loadIndex)
            logger.debug(
                "Project Essentials module found: ${it.simpleName}, name: ${clazz.name}, version: ${clazz.version}"
            )
            modules = modules + clazz
            ModuleEventAPI.fire(ModuleCoreEventType.OnModuleClassProcessed, ModuleEventData(clazz))
        }.run { initialize() }
    }

    private fun initialize() =
        modules.asSequence().sortedWith(compareBy { by -> by.loadIndex }).forEach { module ->
            logger.info("Starting initializing module ${module.name}").also { module.init() }
        }

    private fun processIndexes(index: Int) {
        modules.asSequence().find { it.loadIndex == index }?.let {
            throw ModuleIndexDuplicateException("Module with same load index $index already processed.")
        }
    }
}
