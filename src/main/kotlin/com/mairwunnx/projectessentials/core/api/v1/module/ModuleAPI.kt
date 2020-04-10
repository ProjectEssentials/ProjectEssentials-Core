package com.mairwunnx.projectessentials.core.api.v1.module

import net.minecraftforge.fml.common.Mod

/**
 * Class for interacting with other modules.
 * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
 */
@Suppress("unused")
object ModuleAPI {
    /**
     * @return all installed and checked modules.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun getAllModules() = ModuleProcessor.getModules()

    /**
     * @return module mod id what declared in `@Mod` annotation.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun getModuleModId(module: IModule): String {
        if (module.getModuleData().name == "essentials") {
            return "project_essentials"
        }
        if (module.javaClass.isAnnotationPresent(Mod::class.java)) {
            return module.javaClass.getAnnotation(Mod::class.java).value
        }
        return "project_essentials_${module.getModuleData().name}"
    }

    /**
     * @return module by provided name.
     * @throws ModuleNotFoundException when module not found.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun getModuleByName(name: String) =
        getAllModules().find { it.getModule().getModuleData().name == name }?.let {
            return@let it
        } ?: throw ModuleNotFoundException(
            "Module with name $name not found and not processed."
        )

    /**
     * @param module module class instance.
     * @return true if module existing or installed.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun isModuleExist(module: IModule) = ModuleProcessor.getModules().find {
        it.getModule().getModuleData().name == module.getModule().getModuleData().name
    }.let { return@let it != null }

    /**
     * @param module module name what provided in Module annotation.
     * @return true if module existing or installed.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun isModuleExist(module: String) = ModuleProcessor.getModules().find {
        it.getModule().getModuleData().name == module
    }.let { return@let it != null }
}
