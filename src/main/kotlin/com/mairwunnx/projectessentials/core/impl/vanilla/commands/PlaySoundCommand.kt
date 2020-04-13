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
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ResourceLocationArgument
import net.minecraft.command.arguments.SuggestionProviders
import net.minecraft.command.arguments.Vec3Argument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.network.play.server.SPlaySoundPacket
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import java.lang.Math.pow

internal object PlaySoundCommand : VanillaCommandBase() {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.playsound.failed")
    )

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("playsound")

        val requiredargumentbuilder =
            Commands.argument(
                "sound", ResourceLocationArgument.resourceLocation()
            ).suggests(SuggestionProviders.AVAILABLE_SOUNDS)
        for (soundcategory in SoundCategory.values()) {
            requiredargumentbuilder.then(
                buildCategorySubcommand(
                    soundcategory
                )
            )
        }
        dispatcher.register(
            Commands.literal("playsound").then(requiredargumentbuilder)
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.sound.play", 2)) {
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
                                "native.sound.play", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun buildCategorySubcommand(category: SoundCategory): LiteralArgumentBuilder<CommandSource> {
        return Commands.literal(category.getName()).then(
            Commands.argument(
                "targets", EntityArgument.players()
            ).executes { p_198575_1_ ->
                playSound(
                    p_198575_1_.source,
                    EntityArgument.getPlayers(p_198575_1_, "targets"),
                    ResourceLocationArgument.getResourceLocation(p_198575_1_, "sound"),
                    category,
                    p_198575_1_.source.pos,
                    1.0f, 1.0f, 0.0f
                )
            }.then(
                Commands.argument(
                    "pos", Vec3Argument.vec3()
                ).executes { p_198578_1_: CommandContext<CommandSource> ->
                    playSound(
                        p_198578_1_.source,
                        EntityArgument.getPlayers(p_198578_1_, "targets"),
                        ResourceLocationArgument.getResourceLocation(p_198578_1_, "sound"),
                        category,
                        Vec3Argument.getVec3(p_198578_1_, "pos"),
                        1.0f, 1.0f, 0.0f
                    )
                }.then(
                    Commands.argument(
                        "volume", FloatArgumentType.floatArg(0.0f)
                    ).executes { p_198571_1_: CommandContext<CommandSource> ->
                        playSound(
                            p_198571_1_.source,
                            EntityArgument.getPlayers(p_198571_1_, "targets"),
                            ResourceLocationArgument.getResourceLocation(p_198571_1_, "sound"),
                            category,
                            Vec3Argument.getVec3(p_198571_1_, "pos"),
                            p_198571_1_.getArgument("volume", Float::class.java),
                            1.0f, 0.0f
                        )
                    }.then(
                        Commands.argument(
                            "pitch", FloatArgumentType.floatArg(0.0f, 2.0f)
                        ).executes { p_198574_1_: CommandContext<CommandSource> ->
                            playSound(
                                p_198574_1_.source,
                                EntityArgument.getPlayers(p_198574_1_, "targets"),
                                ResourceLocationArgument.getResourceLocation(p_198574_1_, "sound"),
                                category,
                                Vec3Argument.getVec3(p_198574_1_, "pos"),
                                p_198574_1_.getArgument("volume", Float::class.java),
                                p_198574_1_.getArgument("pitch", Float::class.java),
                                0.0f
                            )
                        }.then(
                            Commands.argument(
                                "minVolume", FloatArgumentType.floatArg(0.0f, 1.0f)
                            ).executes { p_198570_1_: CommandContext<CommandSource> ->
                                playSound(
                                    p_198570_1_.source,
                                    EntityArgument.getPlayers(p_198570_1_, "targets"),
                                    ResourceLocationArgument.getResourceLocation(
                                        p_198570_1_, "sound"
                                    ),
                                    category,
                                    Vec3Argument.getVec3(p_198570_1_, "pos"),
                                    p_198570_1_.getArgument("volume", Float::class.java),
                                    p_198570_1_.getArgument("pitch", Float::class.java),
                                    p_198570_1_.getArgument("minVolume", Float::class.java)
                                )
                            }
                        )
                    )
                )
            )
        )
    }

    @Throws(CommandSyntaxException::class)
    private fun playSound(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        soundIn: ResourceLocation,
        category: SoundCategory,
        pos: Vec3d,
        volume: Float,
        pitch: Float,
        minVolume: Float
    ): Int {
        checkPermissions(source)

        val d0 = pow(if (volume > 1.0f) (volume * 16.0f).toDouble() else 16.0, 2.0)
        var i = 0
        val iterator: Iterator<*> = targets.iterator()
        while (true) {
            var serverplayerentity: ServerPlayerEntity
            var vec3d: Vec3d
            var f: Float
            while (true) {
                if (!iterator.hasNext()) {
                    if (i == 0) throw FAILED_EXCEPTION.create()
                    if (targets.size == 1) {
                        source.sendFeedback(
                            TranslationTextComponent(
                                "commands.playsound.success.single",
                                soundIn,
                                targets.iterator().next().displayName
                            ), true
                        )
                    } else {
                        source.sendFeedback(
                            TranslationTextComponent(
                                "commands.playsound.success.single",
                                soundIn,
                                targets.iterator().next().displayName
                            ), true
                        )
                    }
                    return i
                }
                serverplayerentity = iterator.next() as ServerPlayerEntity
                val d1 = pos.x - serverplayerentity.posX
                val d2 = pos.y - serverplayerentity.posY
                val d3 = pos.z - serverplayerentity.posZ
                val d4 = d1 * d1 + d2 * d2 + d3 * d3
                vec3d = pos
                f = volume
                if (d4 <= d0) break
                if (minVolume > 0.0f) {
                    val d5 = MathHelper.sqrt(d4).toDouble()
                    vec3d = Vec3d(
                        serverplayerentity.posX + d1 / d5 * 2.0,
                        serverplayerentity.posY + d2 / d5 * 2.0,
                        serverplayerentity.posZ + d3 / d5 * 2.0
                    )
                    f = minVolume
                    break
                }
            }
            serverplayerentity.connection.sendPacket(
                SPlaySoundPacket(soundIn, category, vec3d, f, pitch)
            )
            ++i
        }
    }
}
