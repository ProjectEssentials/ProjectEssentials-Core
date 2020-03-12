/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ResourceLocationArgument
import net.minecraft.command.arguments.SuggestionProviders
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.network.play.server.SStopSoundPacket
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object StopSoundCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/stopsound` vanilla command")
        NativeCommandUtils.removeCommand("stopsound")

        val requiredargumentbuilder =
            Commands.argument(
                "targets", EntityArgument.players()
            ).executes { p_198729_0_ ->
                stopSound(
                    p_198729_0_.source,
                    EntityArgument.getPlayers(p_198729_0_, "targets"),
                    null as SoundCategory?,
                    null as ResourceLocation?
                )
            }.then(
                Commands.literal("*").then(
                    Commands.argument(
                        "sound", ResourceLocationArgument.resourceLocation()
                    ).suggests(
                        SuggestionProviders.AVAILABLE_SOUNDS
                    ).executes { p_198732_0_ ->
                        stopSound(
                            p_198732_0_.source,
                            EntityArgument.getPlayers(p_198732_0_, "targets"),
                            null as SoundCategory?,
                            ResourceLocationArgument.getResourceLocation(p_198732_0_, "sound")
                        )
                    }
                )
            )
        for (soundcategory in SoundCategory.values()) {
            requiredargumentbuilder.then(
                Commands.literal(soundcategory.getName()).executes { p_198731_1_ ->
                    stopSound(
                        p_198731_1_.source,
                        EntityArgument.getPlayers(p_198731_1_, "targets"),
                        soundcategory,
                        null as ResourceLocation?
                    )
                }.then(
                    Commands.argument(
                        "sound", ResourceLocationArgument.resourceLocation()
                    ).suggests(
                        SuggestionProviders.AVAILABLE_SOUNDS
                    ).executes { p_198728_1_ ->
                        stopSound(
                            p_198728_1_.source,
                            EntityArgument.getPlayers(p_198728_1_, "targets"),
                            soundcategory,
                            ResourceLocationArgument.getResourceLocation(p_198728_1_, "sound")
                        )
                    }
                )
            )
        }
        dispatcher.register(
            Commands.literal("stopsound").then(requiredargumentbuilder)
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.stopsound", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "stopsound")
                )
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        LocalizationConfigurationUtils.getConfig().enabled,
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                LocalizationConfigurationUtils.getConfig().enabled,
                                "native.command.restricted_hover",
                                "native.stopsound", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun stopSound(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        category: SoundCategory?,
        soundIn: ResourceLocation?
    ): Int {
        checkPermissions(source)

        val sstopsoundpacket = SStopSoundPacket(soundIn, category)
        for (serverplayerentity in targets) {
            serverplayerentity.connection.sendPacket(sstopsoundpacket)
        }
        if (category != null) {
            if (soundIn != null) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.stopsound.success.source.sound",
                        soundIn,
                        category.getName()
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.stopsound.success.source.any",
                        category.getName()
                    ), true
                )
            }
        } else if (soundIn != null) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.stopsound.success.sourceless.sound",
                    soundIn
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent("commands.stopsound.success.sourceless.any"),
                true
            )
        }
        return targets.size
    }
}
