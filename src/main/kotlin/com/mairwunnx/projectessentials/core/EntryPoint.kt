package com.mairwunnx.projectessentials.core

import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.vanilla.commands.AdvancementCommand
import com.mairwunnx.projectessentials.core.vanilla.commands.BanCommand
import com.mairwunnx.projectessentials.core.vanilla.commands.BanIpCommand
import com.mairwunnx.projectessentials.core.vanilla.commands.BanListCommand
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager

@Suppress("unused")
@Mod("project_essentials_core")
internal class EntryPoint : EssBase() {
    private val logger = LogManager.getLogger()

    init {
        modInstance = this
        modVersion = "1.14.4-1.1.0.0"
        logBaseInfo()
        validateForgeVersion()
        MinecraftForge.EVENT_BUS.register(this)
        loadAdditionalModules()
        CommandsConfigurationUtils.loadConfig()
    }

    companion object {
        internal lateinit var modInstance: EntryPoint
        internal var permissionsInstalled: Boolean = false
        internal var cooldownInstalled: Boolean = false

        internal fun hasPermission(
            player: ServerPlayerEntity,
            node: String,
            opLevel: Int = 4
        ): Boolean = if (permissionsInstalled) {
            PermissionsAPI.hasPermission(player.name.string, node)
        } else {
            player.server.opPermissionLevel >= opLevel
        }
    }

    private fun loadAdditionalModules() {
        try {
            Class.forName(
                "com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI"
            )
            permissionsInstalled = true
        } catch (_: ClassNotFoundException) {
            // ignored
        }

        try {
            Class.forName(
                "com.mairwunnx.projectessentials.cooldown.essentials.CooldownAPI"
            )
            cooldownInstalled = true
        } catch (_: ClassNotFoundException) {
            // ignored
        }
    }

    @SubscribeEvent
    internal fun onServerStarting(it: FMLServerStartingEvent) {
        registerNativeCommands(it.server.commandManager.dispatcher)
    }

    private fun registerNativeCommands(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing native vanilla commands")
        AdvancementCommand.register(dispatcher)
        BanCommand.register(dispatcher)
        BanIpCommand.register(dispatcher)
        BanListCommand.register(dispatcher)
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down $modName mod ...")
        CommandsConfigurationUtils.saveConfig()
    }
}
