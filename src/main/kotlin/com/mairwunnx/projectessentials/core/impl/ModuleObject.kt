@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.impl

import com.mairwunnx.projectessentials.core.api.v1.*
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.back.BackLocationAPI
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.asPlayerEntity
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerEntity
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.api.v1.processor.ProcessorAPI
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.core.impl.vanilla.commands.*
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.event.ClickEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import java.io.File

internal class ModuleObject : IModule {
    override val name = this::class.java.`package`.implementationTitle.split("\\s+").last()
    override val version = this::class.java.`package`.implementationVersion!!
    override val loadIndex = 0

    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }

    private var dudeFuckedOff = true

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun init() = initializeModuleSettings()

    @SubscribeEvent
    fun onPortalSpawning(event: BlockEvent.PortalSpawnEvent) {
        if (generalConfiguration.getBool(SETTING_DISABLE_PORTAL_SPAWNING)) {
            event.isCanceled = true
            return
        }
    }

    @SubscribeEvent
    fun onPlayerDeath(event: LivingDeathEvent) {
        if (event.entityLiving.isPlayerEntity) {
            with(event.entityLiving.asPlayerEntity) {
                if (
                    hasPermission(this, "ess.teleport.back.ondeath", 3) ||
                    hasPermission(this, "ess.back.ondeath", 3)
                ) BackLocationAPI.commit(this)
            }
        }
    }

    @SubscribeEvent
    fun onServerStopping(
        @Suppress("UNUSED_PARAMETER")
        event: FMLServerStoppingEvent
    ) = ConfigurationAPI.saveAll()

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        if (generalConfiguration.getBool(SETTING_NATIVE_COMMAND_REPLACE)) {
            registerNativeCommands(event.commandDispatcher, event.server.isDedicatedServer)
        }

        dudeFuckedOff = File(
            projectConfigDirectory + File.separator + "fuck-off-dude.txt"
        ).exists().also { if (!it) printGreetingMessage() }

        CommandAPI.assignDispatcherRoot(event.commandDispatcher)
        CommandAPI.assignDispatcher(event.commandDispatcher)
        ProcessorAPI.getProcessorByName("command").postProcess()
    }

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        BackLocationAPI.revoke(event.player as ServerPlayerEntity)
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
Project Essentials §c§ois not a commercial project §fand all its modules distributed free and not have any restrictions.

§7[ §c-> §7Support the project §6§nhttps://git.io/JfZ1V§7 ]
                    """.trim(),
                    false,
                    clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://git.io/JfZ1V")
                )
            }
        }
    }

    private fun initializeModuleSettings() {
        generalConfiguration.getBoolOrDefault(SETTING_LOC_ENABLED, false)
        generalConfiguration.getStringOrDefault(SETTING_LOC_FALLBACK_LANG, "en_us")
        generalConfiguration.getBoolOrDefault(SETTING_DISABLE_SAFE_ENCHANT, false)
        generalConfiguration.getBoolOrDefault(SETTING_NATIVE_COMMAND_REPLACE, true)
        generalConfiguration.getIntOrDefault(SETTING_LOCATE_COMMAND_FIND_RADIUS, 100)
        generalConfiguration.getBoolOrDefault(SETTING_DISABLE_PORTAL_SPAWNING, false)
        generalConfiguration.getIntOrDefault(SETTING_WEATHER_COMMAND_DEFAULT_DURATION, 6000)
        generalConfiguration.getBoolOrDefault(SETTING_DEOP_COMMAND_REMOVE_OP_PERM, true)
        generalConfiguration.getIntOrDefault(SETTING_LIST_MAX_ELEMENTS_IN_PAGE, 8)

        ConfigureEssentialsCommandAPI.required(SETTING_NATIVE_COMMAND_REPLACE)
        ConfigureEssentialsCommandAPI.required(SETTING_LOC_FALLBACK_LANG)
    }

    private fun printGreetingMessage() = LogManager.getLogger().warn(
        """

Notification from Project Essentials

Project Essentials - the project is based on the enthusiasm of the author, the project is completely free to use and distribute. However, the author needs material support, that is, a donate.
Project Essentials **is not a commercial project** and all its modules distributed free and not have any restrictions.

I will be very happy with your support, below is a link to the donation documentation and how to disable this annoying alert.
For support project you can also put an star on the Project Essentials repository.

Thanks for using my project! </3

[ -> Github organization https://github.com/ProjectEssentials ]
[ -> Support the project https://git.io/JfZ1V ]

        """
    )

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
