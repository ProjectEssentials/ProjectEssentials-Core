package com.mairwunnx.projectessentials.core

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandProcessor
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationProcessor
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI.subscribeOn
import com.mairwunnx.projectessentials.core.api.v1.events.forge.FMLCommonSetupEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModEnqueueEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModProcessEventData
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationProcessor
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleProcessor
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.api.v1.processor.ProcessorAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.impl.ModuleObject
import com.mairwunnx.projectessentials.core.impl.commands.BackLocationCommand
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommand
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.configurations.NativeAliasesConfiguration
import com.mairwunnx.projectessentials.core.impl.events.EventBridge
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import java.io.File

@Suppress("unused")
@Mod("project_essentials_core")
internal class EntryPoint {
    private val logger = LogManager.getLogger()
    private var dudeFuckedOff = true

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

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        dudeFuckedOff = File(
            projectConfigDirectory + File.separator + "fuck-off-dude.txt"
        ).exists()
        CommandAPI.assignDispatcherRoot(event.commandDispatcher)
        CommandAPI.assignDispatcher(event.commandDispatcher)
        ProcessorAPI.getProcessorByName("command").postProcess()
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        if (!dudeFuckedOff) {
            val player = event.player as ServerPlayerEntity

            when {
                hasPermission(player, "ess.notification.support", 4) -> MessagingAPI.sendMessage(
                    player,
                    """
                    §6Notification from §7Project Essentials
                    
                    §fProject Essentials - the project is based on the enthusiasm of the author, the project is completely free to use and distribute. However, the author needs material support, that is, a donate.
                    Project Essentials §c§ois not a commercial project §fand all its modules distributed free of charge and not subject to any restrictions.
                    
                    §7[ §c-> §support the project §6§nhttps://git.io/JfZ1V §7]
                    """.trimIndent(),
                    false
                )
            }
        }
    }
}
