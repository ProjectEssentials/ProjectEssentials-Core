@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.impl

import com.mairwunnx.projectessentials.core.api.v1.*
import com.mairwunnx.projectessentials.core.api.v1.commands.back.BackLocationAPI
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.asPlayerEntity
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerEntity
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.api.v1.module.Module
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.vanilla.commands.*
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent

@OptIn(ExperimentalUnsignedTypes::class)
@Module("core", "2.0.0-SNAPSHOT.1_MC-1.14.4", 0u, "1.0.0")
internal class ModuleCoreObject : IModule {
    private var moduleDataCached: Module? = null
    private val generalConfiguration by lazy {
        ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun init() = initializeModuleSettings()

    fun initializeModuleSettings() {
        generalConfiguration.getBoolOrDefault(SETTING_LOC_ENABLED, false)
        generalConfiguration.getStringOrDefault(SETTING_LOC_FALLBACK_LANG, "en_us")
        generalConfiguration.getBoolOrDefault(SETTING_DISABLE_SAFE_ENCHANT, false)
        generalConfiguration.getBoolOrDefault(SETTING_NATIVE_COMMAND_REPLACE, true)
        generalConfiguration.getIntOrDefault(SETTING_LOCATE_COMMAND_FIND_RADIUS, 100)
        generalConfiguration.getBoolOrDefault(SETTING_DISABLE_PORTAL_SPAWNING, false)
        generalConfiguration.getIntOrDefault(SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000)

        ConfigureEssentialsCommandAPI.required(SETTING_NATIVE_COMMAND_REPLACE)
        ConfigureEssentialsCommandAPI.required(SETTING_LOC_FALLBACK_LANG)
    }

    override fun getModule() = this

    override fun getModuleData(): Module {
        if (moduleDataCached == null) {
            moduleDataCached = this.javaClass.getAnnotation(Module::class.java)
        }
        return moduleDataCached!!
    }

    @SubscribeEvent
    fun onPortalSpawning(event: BlockEvent.PortalSpawnEvent) {
        if (generalConfiguration.getBoolOrDefault(SETTING_DISABLE_PORTAL_SPAWNING, false)) {
            event.isCanceled = true
            return
        }
    }

    @SubscribeEvent
    fun onPlayerDeath(event: LivingDeathEvent) {
        if (event.entityLiving.isPlayerEntity) {
            with(event.entityLiving.asPlayerEntity) {
                if (
                    hasPermission(this, "teleport.back.ondeath", 3) ||
                    hasPermission(this, "ess.back.ondeath", 3)
                ) BackLocationAPI.commit(this)
            }
        }
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        when {
            generalConfiguration.getBoolOrDefault(
                SETTING_NATIVE_COMMAND_REPLACE, true
            ) -> registerNativeCommands(
                event.commandDispatcher,
                event.server.isDedicatedServer
            )
        }
    }

    @SubscribeEvent
    fun onServerStopping(
        @Suppress("UNUSED_PARAMETER")
        event: FMLServerStoppingEvent
    ) = ConfigurationAPI.saveAll()

    private fun registerNativeCommands(
        dispatcher: CommandDispatcher<CommandSource>,
        isDedicatedServer: Boolean
    ) {
        AdvancementCommand.register(dispatcher)
        BossBarCommand.register(dispatcher)
        ClearCommand.register(dispatcher)
        CloneCommand.register(dispatcher)
        DataPackCommand.register(dispatcher)
        DebugCommand.register(dispatcher)
        DefaultGameModeCommand.register(dispatcher)
        DifficultyCommand.register(dispatcher)
        EffectCommand.register(dispatcher)
        EnchantCommand.register(dispatcher)
        ExecuteCommand.register(dispatcher)
        ExperienceCommand.register(dispatcher)
        FillCommand.register(dispatcher)
        ForceLoadCommand.register(dispatcher)
        FunctionCommand.register(dispatcher)
        GameModeCommand.register(dispatcher)
        GameRuleCommand.register(dispatcher)
        GiveCommand.register(dispatcher)
        HelpCommand.register(dispatcher)
        KickCommand.register(dispatcher)
        KillCommand.register(dispatcher)
        ListCommand.register(dispatcher)
        LocateCommand.register(dispatcher)
        LootCommand.register(dispatcher)
        MeCommand.register(dispatcher)
        MessageCommand.register(dispatcher)
        ParticleCommand.register(dispatcher)
        PlaySoundCommand.register(dispatcher)
        PublishCommand.register(dispatcher)
        RecipeCommand.register(dispatcher)
        ReloadCommand.register(dispatcher)
        ReplaceItemCommand.register(dispatcher)
        SayCommand.register(dispatcher)
        ScheduleCommand.register(dispatcher)
        ScoreboardCommand.register(dispatcher)
        SeedCommand.register(dispatcher)
        SetBlockCommand.register(dispatcher)
        SetWorldSpawnCommand.register(dispatcher)
        SpawnPointCommand.register(dispatcher)
        SpreadPlayersCommand.register(dispatcher)
        StopSoundCommand.register(dispatcher)
        SummonCommand.register(dispatcher)
        TagCommand.register(dispatcher)
        TeamCommand.register(dispatcher)
        TeamMsgCommand.register(dispatcher)
        TeleportCommand.register(dispatcher)
        TellRawCommand.register(dispatcher)
        TimeCommand.register(dispatcher)
        TitleCommand.register(dispatcher)
        TriggerCommand.register(dispatcher)
        WeatherCommand.register(dispatcher)
        WorldBorderCommand.register(dispatcher)

        if (isDedicatedServer) {
            BanCommand.register(dispatcher)
            BanIpCommand.register(dispatcher)
            BanListCommand.register(dispatcher)
            DeOpCommand.register(dispatcher)
            OpCommand.register(dispatcher)
            PardonCommand.register(dispatcher)
            PardonIpCommand.register(dispatcher)
            SaveAllCommand.register(dispatcher)
            SaveOffCommand.register(dispatcher)
            SaveOnCommand.register(dispatcher)
            SetIdleTimeoutCommand.register(dispatcher)
            StopCommand.register(dispatcher)
            WhitelistCommand.register(dispatcher)
        }
    }
}
