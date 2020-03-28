package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.configurations.NativeAliasesConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class VanillaCommandBase {
    val logger: Logger = LogManager.getLogger()

    val generalConfiguration =
        ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")

    val configuration =
        ConfigurationAPI.getConfigurationByName<NativeAliasesConfiguration>("native-aliases")
}
