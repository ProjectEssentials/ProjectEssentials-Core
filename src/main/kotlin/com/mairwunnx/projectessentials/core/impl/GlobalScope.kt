package com.mairwunnx.projectessentials.core.impl

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.configurations.NativeMappingsConfiguration

val generalConfiguration by lazy {
    getConfigurationByName<GeneralConfiguration>("general")
}

val nativeMappingsConfiguration by lazy {
    getConfigurationByName<NativeMappingsConfiguration>("native-mappings").take()
}
