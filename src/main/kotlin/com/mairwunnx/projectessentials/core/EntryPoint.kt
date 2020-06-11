@file:Suppress("UNUSED_PARAMETER")

package com.mairwunnx.projectessentials.core

import com.mairwunnx.projectessentials.core.api.v1.IMCLocalizationMessage
import com.mairwunnx.projectessentials.core.api.v1.IMCProvidersMessage
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI.subscribeOn
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModProcessEventData
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.localizationMarker
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.api.v1.providersMarker
import com.mairwunnx.projectessentials.core.impl.ModuleObject
import com.mairwunnx.projectessentials.core.impl.commands.BackLocationCommand
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommand
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.configurations.NativeMappingsConfiguration
import com.mairwunnx.projectessentials.core.impl.events.EventBridge
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent
import org.apache.logging.log4j.LogManager

@Suppress("unused")
@Mod("project_essentials_core")
internal class EntryPoint {
    private val logger = LogManager.getLogger()

    init {
        EventBridge.initialize()
        EVENT_BUS.register(this)
        initProviders()
        initLocalization()
        subscribeEvents()
    }

    @SubscribeEvent
    fun onServerPreStart(event: FMLServerAboutToStartEvent) {
        ConfigurationAPI.loadAll()
        ModuleAPI.initializeOrdered()
    }

    private fun subscribeEvents() {
        subscribeOn<InterModProcessEventData>(
            ForgeEventType.ProcessIMCEvent
        ) { event ->
            processLocalizationRequest(event)
            processProvidersRequest(event)
        }
    }

    private fun initProviders() {
        listOf(
            GeneralConfiguration::class.java,
            NativeMappingsConfiguration::class.java,
            ModuleObject::class.java,
            BackLocationCommand::class.java,
            ConfigureEssentialsCommand::class.java
        ).forEach(ProviderAPI::addProvider)
    }

    private fun initLocalization() {
        LocalizationAPI.apply(this.javaClass) {
            mutableListOf(
                "/assets/projectessentialscore/lang/en_us.json",
                "/assets/projectessentialscore/lang/ru_ru.json",
                "/assets/projectessentialscore/lang/zh_cn.json"
            )
        }
    }

    private fun processLocalizationRequest(event: InterModProcessEventData) {
        event.event.getIMCStream { method ->
            method == IMCLocalizationMessage
        }.also { stream ->
            stream.forEach { message ->
                val clazz = ModList.get().getModContainerById(message.modId).get().mod.javaClass
                message.getMessageSupplier<() -> List<String>>().get().also {
                    logger.debug(
                        localizationMarker, "Localization got from ${message.senderModId}"
                    ).run { LocalizationAPI.apply(clazz, it) }
                }
            }
        }
    }

    private fun processProvidersRequest(event: InterModProcessEventData) {
        event.event.getIMCStream { method ->
            method == IMCProvidersMessage
        }.also { stream ->
            stream.forEach { message ->
                message.getMessageSupplier<() -> List<Class<out Any>>>().get().also {
                    logger.debug(
                        providersMarker, "Providers got from ${message.senderModId}"
                    ).run { it().forEach(ProviderAPI::addProvider) }
                }
            }
        }
    }
}
