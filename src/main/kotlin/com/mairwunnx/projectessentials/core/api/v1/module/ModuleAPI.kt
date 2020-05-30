package com.mairwunnx.projectessentials.core.api.v1.module

import net.minecraftforge.fml.common.Mod

/**
 * Class for interacting with other modules.
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ModuleAPI {
    /**
     * @return all installed and checked modules.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getAllModules() = ModuleProcessor.getModules()

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
        getAllModules().asSequence().find { it.name.toLowerCase() == name.toLowerCase() }?.let {
            return@let it
        } ?: throw ModuleNotFoundException(
            "Module with name $name not found and not processed."
        )

    /**
     * @param module module name what provided in Module annotation.
     * @return true if module existing or installed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun isModuleExist(module: String) = ModuleProcessor.getModules().asSequence().find {
        it.name == module
    }.let { return@let it != null }
}
