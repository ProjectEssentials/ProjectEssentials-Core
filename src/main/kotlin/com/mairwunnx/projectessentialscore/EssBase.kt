package com.mairwunnx.projectessentialscore

import com.mairwunnx.projectessentialscore.extensions.capitalizeWords
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
    var modVersion = "1.14.4-0.0.0.0"
    var modModuleName = "Null"
    var modMaintainer = "MairwunNx (Pavel Erokhin)"
    var modTargetForge = "28.0.X"
    var modTargetForgeRegex = "^28\\.0\\..\\d+|28\\.0\\.[\\d]\$"
    var modTargetMC = "1.14.4"
    var modSources = "https://github.com/ProjectEssentials/ProjectEssentials-Null/"
    var modTelegram = "https://t.me/minecraftforge"

    init {
        modId = this.javaClass.getAnnotation(Mod::class.java).value
        modName = modId.replace("_", " ").capitalizeWords()
        modModuleName = modName.split(" ").last()
        modSources = "https://github.com/ProjectEssentials/ProjectEssentials-$modModuleName/"
    }

    /**
     * Print base modification information to log.
     * @since 1.14.4-1.0.0.0
     */
    fun logBaseInfo() {
        logger.info("$modName starting initializing ...")
        logger.info("    - Mod Id: $modId")
        logger.info("    - Version: $modVersion")
        logger.info("    - Maintainer: $modMaintainer")
        logger.info("    - Target Forge version: $modTargetForge")
        logger.info("    - Target Minecraft version: $modTargetMC")
        logger.info("    - Source code: $modSources")
        logger.info("    - Telegram chat: $modTelegram")
    }

    /**
     * Validate forge version on compatibility with loaded mod.
     * If validation failed, then you will be notified with
     * messages in logger with level WARN.
     * @since 1.14.4-1.0.0.0
     */
    fun validateForgeVersion() {
        logger.info("Checking forge version for compatibility with mod ...")
        if (Regex(modTargetForgeRegex).matches(ForgeVersion.getVersion())) {
            logger.info("Forge version is compatibility with mod.")
        } else {
            logger.warn("Forge version may be incompatible with $modName $modVersion!")
            logger.warn("    - update or downgrade forge version.")
            logger.warn("    - update or downgrade mod version.")
            logger.warn("    - or just create issue on github.")
        }
    }
}
