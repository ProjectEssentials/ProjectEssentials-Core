package com.mairwunnx.projectessentials.core.api.v1.module

/**
 * Class for interacting with other modules.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
@Suppress("unused")
object ModuleAPI {
    /**
     * @return all installed and checked modules.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllModules() = ModuleProcessor.getModules()

    /**
     * @return module by provided name.
     * @throws ModuleNotFoundException when module not found.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getModuleByName(name: String): IModule {
        getAllModules().forEach {
            if (it.getModule().getModuleData().name == name) {
                return it
            }
        }
        throw ModuleNotFoundException(
            "Module with name $name not found and not processed."
        )
    }

    /**
     * @param module module class instance.
     * @return true if module existing or installed.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun isModuleExist(module: IModule): Boolean {
        val name = module.getModule().getModuleData().name

        ModuleProcessor.getModules().forEach {
            if (it.getModule().getModuleData().name == name) {
                return true
            }
        }
        return false
    }

    /**
     * @param module module name what provided in Module annotation.
     * @return true if module existing or installed.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun isModuleExist(module: String): Boolean {
        ModuleProcessor.getModules().forEach {
            if (it.getModule().getModuleData().name == module) {
                return true
            }
        }
        return false
    }
}
