/**
 * This command implementation by Mojang.
 * And decompiled with idea source code was converted
 * to kotlin code.
 * Also added some logic, for example checking on
 * permissions, and for some commands shorten aliases.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands


import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
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
import java.util.function.Function

internal object PublishCommand : VanillaCommandBase() {
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

    private var aliases =
        configuration.take().aliases.publish + "publish"

    private fun tryAssignAliases() {
        CommandAliases.aliases["publish"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("publish")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).requires { p_198583_0_ ->
                    p_198583_0_.server.isSinglePlayer
                }.executes { p_198580_0_ ->
                    publish(
                        p_198580_0_.source,
                        HTTPUtil.getSuitableLanPort()
                    )
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
            if (!hasPermission(source.asPlayer(), "native.publish", 4)) {
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        generalConfiguration.getBool(SETTING_LOC_ENABLED),
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                "native.command.restricted_hover",
                                "native.publish", "4"
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
