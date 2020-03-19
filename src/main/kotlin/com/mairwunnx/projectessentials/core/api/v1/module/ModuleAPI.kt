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
     * @param module module class instance.
     * @return true if module existing or installed.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun isModuleExist(module: IModule): Boolean {
        val name = module.getModuleData().name

        ModuleProcessor.getModules().forEach {
            if (it.getModuleData().name == name) {
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
            if (it.getModuleData().name == module) {
                return true
            }
        }
        return false
    }

    /**
     * @return allowed domains to indexing module classes.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllowedDomains() = ModuleProcessor.getAllowedDomains()
}
