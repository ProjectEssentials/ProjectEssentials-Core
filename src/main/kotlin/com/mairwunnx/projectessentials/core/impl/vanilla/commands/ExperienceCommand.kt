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
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.BiConsumer
import java.util.function.BiPredicate
import java.util.function.ToIntFunction

internal object ExperienceCommand : VanillaCommandBase() {
    private val SET_POINTS_INVALID_EXCEPTION =
        SimpleCommandExceptionType(
            TranslationTextComponent("commands.experience.set.points.invalid")
        )

    private var aliases =
        configuration.take().aliases.experience + "experience"

    private fun tryAssignAliases() {
        CommandAliases.aliases["experience"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("experience")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "targets", EntityArgument.players()
                        ).then(
                            Commands.argument(
                                "amount", IntegerArgumentType.integer()
                            ).executes { p_198445_0_ ->
                                addExperience(
                                    p_198445_0_.source,
                                    EntityArgument.getPlayers(p_198445_0_, "targets"),
                                    IntegerArgumentType.getInteger(p_198445_0_, "amount"),
                                    Type.POINTS
                                )
                            }.then(
                                Commands.literal("points").executes { p_198447_0_ ->
                                    addExperience(
                                        p_198447_0_.source,
                                        EntityArgument.getPlayers(p_198447_0_, "targets"),
                                        IntegerArgumentType.getInteger(p_198447_0_, "amount"),
                                        Type.POINTS
                                    )
                                }
                            ).then(
                                Commands.literal("levels").executes { p_198436_0_ ->
                                    addExperience(
                                        p_198436_0_.source,
                                        EntityArgument.getPlayers(p_198436_0_, "targets"),
                                        IntegerArgumentType.getInteger(p_198436_0_, "amount"),
                                        Type.LEVELS
                                    )
                                }
                            )
                        )
                    )
                ).then(
                    Commands.literal("set").then(
                        Commands.argument(
                            "targets", EntityArgument.players()
                        ).then(Commands.argument(
                            "amount", IntegerArgumentType.integer(0)
                        ).executes { p_198439_0_ ->
                            setExperience(
                                p_198439_0_.source,
                                EntityArgument.getPlayers(p_198439_0_, "targets"),
                                IntegerArgumentType.getInteger(p_198439_0_, "amount"),
                                Type.POINTS
                            )
                        }.then(
                            Commands.literal("points").executes { p_198444_0_ ->
                                setExperience(
                                    p_198444_0_.source,
                                    EntityArgument.getPlayers(p_198444_0_, "targets"),
                                    IntegerArgumentType.getInteger(p_198444_0_, "amount"),
                                    Type.POINTS
                                )
                            }
                        ).then(
                            Commands.literal("levels").executes { p_198440_0_ ->
                                setExperience(
                                    p_198440_0_.source,
                                    EntityArgument.getPlayers(p_198440_0_, "targets"),
                                    IntegerArgumentType.getInteger(p_198440_0_, "amount"),
                                    Type.LEVELS
                                )
                            }
                        )
                        )
                    )
                ).then(
                    Commands.literal("query").then(
                        Commands.argument(
                            "targets", EntityArgument.player()
                        ).then(
                            Commands.literal("points").executes { p_198435_0_ ->
                                queryExperience(
                                    p_198435_0_.source,
                                    EntityArgument.getPlayer(p_198435_0_, "targets"),
                                    Type.POINTS
                                )
                            }
                        ).then(
                            Commands.literal("levels").executes { p_198446_0_ ->
                                queryExperience(
                                    p_198446_0_.source,
                                    EntityArgument.getPlayer(p_198446_0_, "targets"),
                                    Type.LEVELS
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("show").then(
                        Commands.argument(
                            "targets", EntityArgument.player()
                        ).then(
                            Commands.literal("points").executes { p_198435_0_ ->
                                queryExperience(
                                    p_198435_0_.source,
                                    EntityArgument.getPlayer(p_198435_0_, "targets"),
                                    Type.POINTS
                                )
                            }
                        ).then(
                            Commands.literal("levels").executes { p_198446_0_ ->
                                queryExperience(
                                    p_198446_0_.source,
                                    EntityArgument.getPlayer(p_198446_0_, "targets"),
                                    Type.LEVELS
                                )
                            }
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource, action: String) {
        try {
            if (!hasPermission(source.asPlayer(), "native.experience.$action", 2)) {
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
                                "native.experience.$action", "2"
                            )
                        )
                    )
                )
            }
        } catch (_: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun queryExperience(
        source: CommandSource,
        player: ServerPlayerEntity,
        type: Type
    ): Int {
        checkPermissions(source, "query")

        val i = type.xpGetter.applyAsInt(player)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.experience.query." + type.baseName,
                player.displayName,
                i
            ), false
        )
        return i
    }

    private fun addExperience(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        amount: Int,
        type: Type
    ): Int {
        checkPermissions(source, "add")

        for (serverplayerentity in targets) {
            type.xpAdder.accept(serverplayerentity, amount)
        }
        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.experience.add." + type.baseName + ".success.single",
                    amount, targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.experience.add." + type.baseName + ".success.multiple",
                    amount, targets.size
                ), true
            )
        }
        return targets.size
    }

    @Throws(CommandSyntaxException::class)
    private fun setExperience(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        amount: Int,
        type: Type
    ): Int {
        checkPermissions(source, "set")

        var i = 0
        for (serverplayerentity in targets) {
            if (type.xpSetter.test(serverplayerentity, amount)) {
                ++i
            }
        }
        return if (i == 0) {
            throw SET_POINTS_INVALID_EXCEPTION.create()
        } else {
            if (targets.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.experience.set." + type.baseName + ".success.single",
                        amount, targets.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.experience.set." + type.baseName + ".success.multiple",
                        amount, targets.size
                    ), true
                )
            }
            targets.size
        }
    }

    internal enum class Type(
        val baseName: String,
        val xpAdder: BiConsumer<ServerPlayerEntity, Int>,
        val xpSetter: BiPredicate<ServerPlayerEntity, Int>,
        val xpGetter: ToIntFunction<ServerPlayerEntity>
    ) {
        POINTS("points",
            BiConsumer<ServerPlayerEntity, Int> { obj, p_195068_1_ ->
                obj.giveExperiencePoints(p_195068_1_)
            },
            BiPredicate<ServerPlayerEntity, Int> { p_198424_0_, p_198424_1_ ->
                if (p_198424_1_ >= p_198424_0_.xpBarCap()) {
                    false
                } else {
                    p_198424_0_.func_195394_a(p_198424_1_)
                    true
                }
            },
            ToIntFunction<ServerPlayerEntity> { p_198422_0_ ->
                MathHelper.floor(
                    p_198422_0_.experience * p_198422_0_.xpBarCap().toFloat()
                )
            }
        ),
        LEVELS("levels",
            BiConsumer<ServerPlayerEntity, Int> { obj, levels ->
                obj.addExperienceLevel(levels)
            },
            BiPredicate<ServerPlayerEntity, Int> { p_198425_0_, p_198425_1_ ->
                p_198425_0_.setExperienceLevel(p_198425_1_)
                true
            },
            ToIntFunction<ServerPlayerEntity> { it.experienceLevel }
        )
    }
}

