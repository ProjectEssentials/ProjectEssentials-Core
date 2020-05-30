package com.mairwunnx.projectessentials.core.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_CORE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI.requiredServerRestart
import com.mairwunnx.projectessentials.core.impl.commands.arguments.ConfigurationKeyArgument
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

/**
 * Configure essentials command api, basically stores
 * only [requiredServerRestart] list with setting names
 * what after change requires server restart.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused")
object ConfigureEssentialsCommandAPI {
    private val requiredServerRestart = mutableListOf<String>()

    /**
     * Adds setting to list with configurations what after change
     * requires server restart or configuration reloading.
     *
     * @param setting setting name.
     * @return true if setting added.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun required(setting: String) = requiredServerRestart.add(setting)

    /**
     * @param setting setting name.
     * @return true if [setting] is requires server restart
     * after value change.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun isRequired(setting: String) = requiredServerRestart.contains(setting)

    /**
     * @return list with settings what requires server restart
     * after value changing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getRequired() = requiredServerRestart.toList()
}

internal object ConfigureEssentialsCommand : CommandBase(
    configureEssentialsLiteral, false
) {
    private val generalConfiguration by lazy {
        ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")
    }

    override val name = "configure-essentials"

    /*
        This is a correction of the problem in order to get the list
        of settings in the configuration several times, because for
        the first time it is empty.
     */
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        this.literal = configureEssentialsLiteral
        super.register(dispatcher)
    }

    override fun process(context: CommandContext<CommandSource>): Int {
        val setting = ConfigurationKeyArgument.getValue(context, "setting")
        val value = CommandAPI.getString(context, "value")
        val oldValue = generalConfiguration.take().getValue(setting).toString()

        if (context.isPlayerSender()) {
            if (hasPermission(context.getPlayer()!!, "ess.configure.essentials.$setting", 4)) {
                if (ConfigureEssentialsCommandAPI.isRequired(setting)) {
                    LogManager.getLogger().info(
                        "Setting name `$setting` value changed by ${context.playerName()} from `$oldValue` to $value, but restart required for applying changes."
                    )
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "$MESSAGE_CORE_PREFIX.configure.successfully_required_restart",
                        args = *arrayOf(setting, oldValue, value)
                    )
                } else {
                    LogManager.getLogger().info(
                        "Setting name `$setting` value changed by ${context.playerName()} from `$oldValue` to $value"
                    )
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "$MESSAGE_CORE_PREFIX.configure.successfully",
                        args = *arrayOf(setting, oldValue, value)
                    )
                }
                generalConfiguration.put(setting, value)
                super.process(context)
            } else {
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    "$MESSAGE_CORE_PREFIX.configure.restricted",
                    args = *arrayOf(setting)
                )
            }
        } else {
            if (ConfigureEssentialsCommandAPI.isRequired(setting)) {
                ServerMessagingAPI.response(
                    "Setting name `$setting` value changed from `$oldValue` to $value, but restart required for applying changes."
                )
            } else {
                ServerMessagingAPI.response(
                    "Setting name `$setting` value changed from `$oldValue` to $value"
                )
            }
            generalConfiguration.put(setting, value)
            super.process(context)
        }
        return 0
    }
}
