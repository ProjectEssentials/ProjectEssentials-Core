package com.mairwunnx.projectessentials.core.configuration.commands

import com.mairwunnx.projectessentials.core.helpers.MOD_CONFIG_FOLDER
import com.mairwunnx.projectessentials.core.helpers.jsonInstance
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

internal object CommandsConfigurationUtils {
    private val logger = LogManager.getLogger()
    private var configuration = CommandsConfiguration()
    private val COMMANDS_CONFIG = MOD_CONFIG_FOLDER + File.separator + "native-commands.json"

    internal fun loadConfig() {
        logger.info("Loading native vanilla commands configuration")

        try {
            val configRaw = File(COMMANDS_CONFIG).readText()
            configuration = jsonInstance.parse(CommandsConfiguration.serializer(), configRaw)
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($COMMANDS_CONFIG) not found!")
            logger.warn("The default configuration will be used")
        }
    }

    internal fun saveConfig() {
        File(MOD_CONFIG_FOLDER).mkdirs()

        logger.info("Saving confuguration for native vanilla commands")

        val commandsConfigRaw = jsonInstance.stringify(
            CommandsConfiguration.serializer(),
            configuration
        )

        try {
            File(COMMANDS_CONFIG).writeText(commandsConfigRaw)
        } catch (ex: SecurityException) {
            logger.error("An error occurred while saving commands configuration", ex)
        }
    }

    internal fun getConfig() = configuration
}
