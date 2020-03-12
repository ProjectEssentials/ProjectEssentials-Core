package com.mairwunnx.projectessentials.core

import com.mairwunnx.projectessentials.core.extensions.capitalizeWords
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.versions.forge.ForgeVersion
import org.apache.logging.log4j.LogManager

/**
 * Base Project Essentials modification class.
 * @since 1.14.4-1.0.0.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class EssBase {
    private val logger = LogManager.getLogger()

    var modId = "project_essentials_null"
    var modName = "Project Essentials Null"
    var modVersion = "1.14.4-0.0.0"
    var modModuleName = "Null"
    var modMaintainer = "MairwunNx (Pavel Erokhin)"
    var modTargetForge = "28.2.X"
    var modTargetForgeRegex = "^28\\.0\\.[\\d]+|28\\.1\\.[\\d]+$|28\\.2\\.[\\d]+\$"
    var modTargetMC = "1.14.4"
    var modSources = "https://github.com/ProjectEssentials/ProjectEssentials-Null/"
    var modTelegram = "https://t.me/minecraftforge"
    var modCurseForge = "Null"

    init {
        modId = this.javaClass.getAnnotation(Mod::class.java).value
        modName = modId.replace("_", " ").capitalizeWords()
        modModuleName = modName.split(" ").last()
        modSources = "https://github.com/ProjectEssentials/ProjectEssentials-$modModuleName/"
        modCurseForge =
            "https://www.curseforge.com/minecraft/mc-mods/project-essentials-${modModuleName.toLowerCase()}"
    }

    /**
     * Print base modification information to log.
     * @since 1.14.4-1.0.0.0
     */
    fun logBaseInfo() {
        logger.info(
            "\n" +
                    "            **** $modName starting initializing ****\n\n" +
                    "    - Mod Id: $modId\n" +
                    "    - Version: $modVersion\n" +
                    "    - Maintainer: $modMaintainer\n" +
                    "    - Target Forge version: $modTargetForge\n" +
                    "    - Target Minecraft version: $modTargetMC\n" +
                    "    - Source code: $modSources\n" +
                    "    - Telegram chat: $modTelegram\n" +
                    "    - CurseForge: $modCurseForge" + "\n"
        )
    }

    /**
     * Validate forge version on compatibility with loaded mod.
     * If validation failed, then you will be notified with
     * messages in logger with level WARN.
     * @since 1.14.4-1.0.0.0
     */
    fun validateForgeVersion() {
        if (!Regex(modTargetForgeRegex).matches(ForgeVersion.getVersion())) {
            logger.warn(
                "\n            **** Forge version may be incompatible with $modName $modVersion! ****\n\n" +
                        "    - update or downgrade forge version.\n" +
                        "    - update or downgrade mod version.\n" +
                        "    - or just create issue on github." + "\n"
            )
        }
    }

    companion object {
        /**
         * Base class path of Project Essentials,
         * includes domain, maintainer and project name.
         * @since 1.14.4-1.2.1
         */
        const val baseClassPath = "com.mairwunnx.projectessentials"

        /**
         * Class path of PermissionAPI module.
         * @since 1.14.4-1.2.1
         */
        const val permissionAPIClassPath = "$baseClassPath.permissions.permissions.PermissionsAPI"

        /**
         * Class path of CooldownAPI module.
         * @since 1.14.4-1.2.1
         */
        const val cooldownAPIClassPath = "$baseClassPath.cooldown.essentials.CooldownAPI"
    }
}
