package com.mairwunnx.projectessentials.core.impl

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration

val generalConfiguration by lazy {
    getConfigurationByName<GeneralConfiguration>("general")
}
