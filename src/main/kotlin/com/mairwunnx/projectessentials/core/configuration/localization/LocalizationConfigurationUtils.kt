package com.mairwunnx.projectessentials.core.configuration.localization

import com.mairwunnx.projectessentials.core.helpers.MOD_CONFIG_FOLDER
import com.mairwunnx.projectessentials.core.helpers.jsonInstance
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

object LocalizationConfigurationUtils {
    private var initialized = false
    private val logger = LogManager.getLogger()
    private var configuration = LocalizationConfiguration()
    private val LOCALIZATION_CONFIG = MOD_CONFIG_FOLDER + File.separator + "localization.json"

    internal fun loadConfig() {
        if (!initialized) {
            logger.info("Loading localization configuration")
            initialized = true

            try {
                val configRaw = File(LOCALIZATION_CONFIG).readText()
                configuration =
                    jsonInstance.parse(LocalizationConfiguration.serializer(), configRaw)
            } catch (ex: FileNotFoundException) {
                logger.error("Configuration file ($LOCALIZATION_CONFIG) not found!")
                logger.warn("The default configuration will be used")
            }
        }
    }

    internal fun saveConfig() {
        File(MOD_CONFIG_FOLDER).mkdirs()

        logger.info("Saving localization configuration")

        val localizationConfigRaw = jsonInstance.stringify(
            LocalizationConfiguration.serializer(), configuration
        )

        try {
            File(LOCALIZATION_CONFIG).writeText(localizationConfigRaw)
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving localization configuration", ex)
        }
    }

    /**
     * @return localization configuration instance.
     */
    fun getConfig(): LocalizationConfiguration {
        if (!initialized) loadConfig()
        return configuration
    }
}
