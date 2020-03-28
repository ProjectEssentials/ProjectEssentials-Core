package com.mairwunnx.projectessentials.core

import com.github.matfax.klassindex.KlassIndex
import com.mairwunnx.projectessentials.core.api.v1.commands.Command
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandProcessor
import com.mairwunnx.projectessentials.core.api.v1.configuration.Configuration
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationProcessor
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI.subscribeOn
import com.mairwunnx.projectessentials.core.api.v1.events.forge.FMLCommonSetupEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModEnqueueEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModProcessEventData
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationProcessor
import com.mairwunnx.projectessentials.core.api.v1.module.Module
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleProcessor
import com.mairwunnx.projectessentials.core.api.v1.processor.ProcessorAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.impl.events.EventBridge
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager

@Suppress("unused")
@Mod(value = "project_essentials_core")
internal class EntryPoint {
    private val logger = LogManager.getLogger()

    init {
        initializeProviders()
        EventBridge.initialize()
        subscribeEvents()
        EVENT_BUS.register(this)
    }

    private fun initializeProviders() {
        KlassIndex.getAnnotated(Configuration::class).forEach {
            ProviderAPI.addProvider(it)
        }

        KlassIndex.getAnnotated(Module::class).forEach {
            ProviderAPI.addProvider(it)
        }

        KlassIndex.getAnnotated(Command::class).forEach {
            ProviderAPI.addProvider(it)
        }
    }

    private fun subscribeEvents() {
        subscribeOn<FMLCommonSetupEventData>(
            ForgeEventType.SetupEvent
        ) {
            applyLocalization()
            registerProcessors()
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
                ),
                "core",
                EntryPoint::class.java
            )
        )
    }

    private fun registerProcessors() {
        ProcessorAPI.register(ConfigurationProcessor)
        ProcessorAPI.register(ModuleProcessor)
        ProcessorAPI.register(LocalizationProcessor)
        ProcessorAPI.register(CommandProcessor)
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        CommandAPI.assignDispatcherRoot(event.commandDispatcher)
        CommandAPI.assignDispatcher(event.commandDispatcher)
        ProcessorAPI.getProcessorByName("command").postProcess()
    }
}
