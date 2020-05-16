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
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ParticleArgument
import net.minecraft.command.arguments.Vec3Argument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.particles.IParticleData
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object ParticleCommand : VanillaCommandBase() {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.particle.failed")
    )

    private var aliases = configuration.take().aliases.particle + "particle"

    private fun tryAssignAliases() {
        CommandAliases.aliases["particle"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("particle")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "name", ParticleArgument.particle()
                    ).executes { p_198562_0_ ->
                        spawnParticle(
                            p_198562_0_.source,
                            ParticleArgument.getParticle(p_198562_0_, "name"),
                            p_198562_0_.source.pos,
                            Vec3d.ZERO,
                            0.0f,
                            0,
                            false,
                            p_198562_0_.source.server.playerList.players
                        )
                    }.then(
                        Commands.argument(
                            "pos", Vec3Argument.vec3()
                        ).executes { p_201226_0_ ->
                            spawnParticle(
                                p_201226_0_.source,
                                ParticleArgument.getParticle(p_201226_0_, "name"),
                                Vec3Argument.getVec3(p_201226_0_, "pos"),
                                Vec3d.ZERO,
                                0.0f,
                                0,
                                false,
                                p_201226_0_.source.server.playerList.players
                            )
                        }.then(
                            Commands.argument(
                                "delta", Vec3Argument.vec3(false)
                            ).then(
                                Commands.argument(
                                    "speed", FloatArgumentType.floatArg(0.0f)
                                ).then(
                                    Commands.argument(
                                        "count", IntegerArgumentType.integer(0)
                                    ).executes { p_198565_0_ ->
                                        spawnParticle(
                                            p_198565_0_.source,
                                            ParticleArgument.getParticle(p_198565_0_, "name"),
                                            Vec3Argument.getVec3(p_198565_0_, "pos"),
                                            Vec3Argument.getVec3(p_198565_0_, "delta"),
                                            FloatArgumentType.getFloat(p_198565_0_, "speed"),
                                            IntegerArgumentType.getInteger(p_198565_0_, "count"),
                                            false,
                                            p_198565_0_.source.server.playerList.players
                                        )
                                    }.then(
                                        Commands.literal("force").executes { p_198561_0_ ->
                                            spawnParticle(
                                                p_198561_0_.source,
                                                ParticleArgument.getParticle(p_198561_0_, "name"),
                                                Vec3Argument.getVec3(p_198561_0_, "pos"),
                                                Vec3Argument.getVec3(p_198561_0_, "delta"),
                                                FloatArgumentType.getFloat(p_198561_0_, "speed"),
                                                IntegerArgumentType.getInteger(
                                                    p_198561_0_, "count"
                                                ),
                                                true,
                                                p_198561_0_.source.server.playerList.players
                                            )
                                        }.then(
                                            Commands.argument(
                                                "viewers", EntityArgument.players()
                                            ).executes { p_198566_0_ ->
                                                spawnParticle(
                                                    p_198566_0_.source,
                                                    ParticleArgument.getParticle(
                                                        p_198566_0_,
                                                        "name"
                                                    ),
                                                    Vec3Argument.getVec3(p_198566_0_, "pos"),
                                                    Vec3Argument.getVec3(p_198566_0_, "delta"),
                                                    FloatArgumentType.getFloat(
                                                        p_198566_0_,
                                                        "speed"
                                                    ),
                                                    IntegerArgumentType.getInteger(
                                                        p_198566_0_,
                                                        "count"
                                                    ),
                                                    true,
                                                    EntityArgument.getPlayers(
                                                        p_198566_0_,
                                                        "viewers"
                                                    )
                                                )
                                            }
                                        )
                                    ).then(
                                        Commands.literal("normal").executes { p_198560_0_ ->
                                            spawnParticle(
                                                p_198560_0_.source,
                                                ParticleArgument.getParticle(p_198560_0_, "name"),
                                                Vec3Argument.getVec3(p_198560_0_, "pos"),
                                                Vec3Argument.getVec3(p_198560_0_, "delta"),
                                                FloatArgumentType.getFloat(p_198560_0_, "speed"),
                                                IntegerArgumentType.getInteger(
                                                    p_198560_0_, "count"
                                                ),
                                                false,
                                                p_198560_0_.source.server.playerList.players
                                            )
                                        }.then(
                                            Commands.argument(
                                                "viewers", EntityArgument.players()
                                            )
                                                .executes { p_198567_0_: CommandContext<CommandSource> ->
                                                    spawnParticle(
                                                        p_198567_0_.source,
                                                        ParticleArgument.getParticle(
                                                            p_198567_0_, "name"
                                                        ),
                                                        Vec3Argument.getVec3(p_198567_0_, "pos"),
                                                        Vec3Argument.getVec3(p_198567_0_, "delta"),
                                                        FloatArgumentType.getFloat(
                                                            p_198567_0_, "speed"
                                                        ),
                                                        IntegerArgumentType.getInteger(
                                                            p_198567_0_, "count"
                                                        ),
                                                        false,
                                                        EntityArgument.getPlayers(
                                                            p_198567_0_, "viewers"
                                                        )
                                                    )
                                                }
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.particle", 2)) {
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
                                "native.particle", "2"
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
    private fun spawnParticle(
        source: CommandSource,
        particleData: IParticleData,
        pos: Vec3d,
        delta: Vec3d,
        speed: Float,
        count: Int,
        force: Boolean,
        viewers: Collection<ServerPlayerEntity>
    ): Int {
        checkPermissions(source)

        var i = 0
        for (serverplayerentity in viewers) {
            if (source.world.spawnParticle(
                    serverplayerentity,
                    particleData,
                    force,
                    pos.x, pos.y, pos.z,
                    count,
                    delta.x, delta.y, delta.z,
                    speed.toDouble()
                )
            ) ++i
        }
        return if (i == 0) {
            throw FAILED_EXCEPTION.create()
        } else {
            @Suppress("DEPRECATION")
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.particle.success",
                    Registry.PARTICLE_TYPE.getKey(particleData.type).toString()
                ), true
            )
            i
        }
    }
}
