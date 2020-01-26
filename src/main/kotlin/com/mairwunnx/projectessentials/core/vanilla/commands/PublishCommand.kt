/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.HTTPUtil
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager
import java.util.function.Function

internal object PublishCommand {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.publish.failed")
    )
    private val ALREADY_PUBLISHED_EXCEPTION =
        DynamicCommandExceptionType(
            Function { p_208900_0_: Any? ->
                TranslationTextComponent(
                    "commands.publish.alreadyPublished", p_208900_0_
                )
            }
        )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.publish + "publish"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["publish"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/publish` vanilla command")
        NativeCommandUtils.removeCommand("publish")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).requires { p_198583_0_ ->
                    p_198583_0_.server.isSinglePlayer
                }.executes { p_198580_0_ ->
                    publish(p_198580_0_.source, HTTPUtil.getSuitableLanPort())
                }.then(
                    Commands.argument(
                        "port", IntegerArgumentType.integer(0, 65535)
                    ).executes { p_198582_0_ ->
                        publish(
                            p_198582_0_.source,
                            IntegerArgumentType.getInteger(p_198582_0_, "port")
                        )
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.publish", 4)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "publish")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.publish", "4"
                                )
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun publish(source: CommandSource, port: Int): Int {
        checkPermissions(source)
        return if (source.server.public) {
            throw ALREADY_PUBLISHED_EXCEPTION.create(source.server.serverPort)
        } else if (!source.server.shareToLAN(source.server.gameType, false, port)) {
            throw FAILED_EXCEPTION.create()
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.publish.success", port
                ), true
            )
            port
        }
    }
}
