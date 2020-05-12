package com.mairwunnx.projectessentials.core

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandProcessor
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationProcessor
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI.subscribeOn
import com.mairwunnx.projectessentials.core.api.v1.events.forge.FMLCommonSetupEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModEnqueueEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModProcessEventData
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationProcessor
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleProcessor
import com.mairwunnx.projectessentials.core.api.v1.processor.ProcessorAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.impl.ModuleObject
import com.mairwunnx.projectessentials.core.impl.commands.BackLocationCommand
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommand
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.configurations.NativeAliasesConfiguration
import com.mairwunnx.projectessentials.core.impl.events.EventBridge
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod("project_essentials_core")
internal class EntryPoint {
    /*
        Sorry for hardcoded classes in this list.
        I tried getting this classes with library https://github.com/matfax/klassindex
        but it incorrectly works with multi-module projects,
        for example if core will using klassindex library, then
        other modules will incorrectly finding annotated classes,
        because same class path of generated object with class references.

        If you know how resolve it, please, open issue or pull request
        and we can discuss it.
     */
    private val providers = listOf(
        GeneralConfiguration::class,
        NativeAliasesConfiguration::class,
        ModuleObject::class,
        BackLocationCommand::class,
        ConfigureEssentialsCommand::class
    )

    private val processors = listOf(
        ConfigurationProcessor,
        ModuleProcessor,
        LocalizationProcessor,
        CommandProcessor
    )

    init {
        EventBridge.initialize()
        providers.forEach { ProviderAPI.addProvider(it) }
        subscribeEvents()
        EVENT_BUS.register(this)
    }

    private fun subscribeEvents() {
        subscribeOn<FMLCommonSetupEventData>(
            ForgeEventType.SetupEvent
        ) {
            applyLocalization()
            processors.forEach { ProcessorAPI.register(it) }
        }

        subscribeOn<InterModEnqueueEventData>(
            ForgeEventType.EnqueueIMCEvent
        ) {
            ProcessorAPI.processProcessors()
        }

        subscribeOn<InterModProcessEventData>(
            ForgeEventType.ProcessIMCEvent
        ) {
            ProcessorAPI.postProcessProcessors()
        }
    }

    private fun applyLocalization() {
        LocalizationAPI.apply(
            Localization(
                mutableListOf(
                    "/assets/projectessentialscore/lang/en_us.json",
                    "/assets/projectessentialscore/lang/ru_ru.json"
                ), "core", EntryPoint::class.java
            )
        )
    }
}
