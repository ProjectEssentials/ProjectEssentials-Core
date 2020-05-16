/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.ComponentArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.network.play.server.STitlePacket
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import java.util.*

internal object TitleCommand : VanillaCommandBase() {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("title")

        dispatcher.register(
            Commands.literal("title").then(
                Commands.argument(
                    "targets", EntityArgument.players()
                ).then(
                    Commands.literal("clear").executes { p_198838_0_ ->
                        clear(
                            p_198838_0_.source,
                            EntityArgument.getPlayers(p_198838_0_, "targets")
                        )
                    }
                ).then(
                    Commands.literal("reset").executes { p_198841_0_ ->
                        reset(
                            p_198841_0_.source,
                            EntityArgument.getPlayers(p_198841_0_, "targets")
                        )
                    }
                ).then(
                    Commands.literal("title").then(
                        Commands.argument(
                            "title", ComponentArgument.component()
                        ).executes { p_198837_0_ ->
                            show(
                                p_198837_0_.source,
                                EntityArgument.getPlayers(p_198837_0_, "targets"),
                                ComponentArgument.getComponent(p_198837_0_, "title"),
                                STitlePacket.Type.TITLE
                            )
                        }
                    )
                ).then(
                    Commands.literal("subtitle").then(
                        Commands.argument(
                            "title", ComponentArgument.component()
                        ).executes { p_198842_0_ ->
                            show(
                                p_198842_0_.source,
                                EntityArgument.getPlayers(p_198842_0_, "targets"),
                                ComponentArgument.getComponent(p_198842_0_, "title"),
                                STitlePacket.Type.SUBTITLE
                            )
                        }
                    )
                ).then(
                    Commands.literal("actionbar").then(
                        Commands.argument(
                            "title", ComponentArgument.component()
                        ).executes { p_198836_0_ ->
                            show(
                                p_198836_0_.source,
                                EntityArgument.getPlayers(p_198836_0_, "targets"),
                                ComponentArgument.getComponent(p_198836_0_, "title"),
                                STitlePacket.Type.ACTIONBAR
                            )
                        }
                    )
                ).then(
                    Commands.literal("times").then(
                        Commands.argument(
                            "fadeIn", IntegerArgumentType.integer(0)
                        ).then(
                            Commands.argument(
                                "stay", IntegerArgumentType.integer(0)
                            ).then(
                                Commands.argument(
                                    "fadeOut", IntegerArgumentType.integer(0)
                                ).executes { p_198843_0_ ->
                                    setTimes(
                                        p_198843_0_.source,
                                        EntityArgument.getPlayers(p_198843_0_, "targets"),
                                        IntegerArgumentType.getInteger(p_198843_0_, "fadeIn"),
                                        IntegerArgumentType.getInteger(p_198843_0_, "stay"),
                                        IntegerArgumentType.getInteger(p_198843_0_, "fadeOut")
                                    )
                                }
                            )
                        )
                    )
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.title", 2)) {
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
                                "native.title", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun clear(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>
    ): Int {
        checkPermissions(source)

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val stitlepacket = STitlePacket(STitlePacket.Type.CLEAR, null as ITextComponent?)
        for (serverplayerentity in targets) {
            serverplayerentity.connection.sendPacket(stitlepacket)
        }
        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.cleared.single",
                    targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.cleared.multiple",
                    targets.size
                ), true
            )
        }
        return targets.size
    }

    private fun reset(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>
    ): Int {
        checkPermissions(source)

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val stitlepacket = STitlePacket(STitlePacket.Type.RESET, null as ITextComponent?)
        for (serverplayerentity in targets) {
            serverplayerentity.connection.sendPacket(stitlepacket)
        }
        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.reset.single",
                    targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.reset.multiple",
                    targets.size
                ), true
            )
        }
        return targets.size
    }

    @Throws(CommandSyntaxException::class)
    private fun show(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        message: ITextComponent,
        type: STitlePacket.Type
    ): Int {
        checkPermissions(source)

        for (serverplayerentity in targets) {
            serverplayerentity.connection.sendPacket(
                STitlePacket(
                    type,
                    TextComponentUtils.updateForEntity(
                        source, message, serverplayerentity, 0
                    )
                )
            )
        }
        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.show." + type.name.toLowerCase(
                        Locale.ROOT
                    ) + ".single", targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.show." + type.name.toLowerCase(
                        Locale.ROOT
                    ) + ".multiple", targets.size
                ), true
            )
        }
        return targets.size
    }

    private fun setTimes(
        source: CommandSource,
        target: Collection<ServerPlayerEntity>,
        fadeIn: Int,
        stay: Int,
        fadeOut: Int
    ): Int {
        checkPermissions(source)

        val stitlepacket = STitlePacket(fadeIn, stay, fadeOut)
        for (serverplayerentity in target) {
            serverplayerentity.connection.sendPacket(stitlepacket)
        }
        if (target.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.times.single",
                    target.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.title.times.multiple",
                    target.size
                ), true
            )
        }
        return target.size
    }
}
