package com.mairwunnx.projectessentials.core.impl.configurations

import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.helpers.jsonInstance
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

object NativeMappingsConfiguration : IConfiguration<NativeMappingsConfigurationModel> {
    private val logger = LogManager.getLogger()
    private var configurationData = NativeMappingsConfigurationModel()

    override val name = "native-mappings"
    override val version = 1
    override val configuration = take()
    override val path = projectConfigDirectory + File.separator + "native-mappings.json"

    override fun load() {
        try {
            val configRaw = File(path).readText()
            configurationData = jsonInstance.parse(
                NativeMappingsConfigurationModel.serializer(), configRaw
            )
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($path) not found!")
            logger.warn("The default configuration will be used")
        }
    }

    override fun save() {
        File(path).parentFile.mkdirs()

        logger.info("Saving configuration `${name}`")
        val raw = jsonInstance.stringify(
            NativeMappingsConfigurationModel.serializer(), configurationData
        )
        try {
            File(path).writeText(raw)
        } catch (ex: SecurityException) {
            logger.error(
                "An error occurred while saving commands configuration", ex
            )
        }
    }

    override fun take() = configurationData
}
