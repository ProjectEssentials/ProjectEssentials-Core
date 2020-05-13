package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.configurations.NativeAliasesConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class VanillaCommandBase {
    val logger: Logger = LogManager.getLogger()
    val generalConfiguration = getConfigurationByName<GeneralConfiguration>("general")
    val configuration = getConfigurationByName<NativeAliasesConfiguration>("native-aliases")
}
