package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.Vec2Argument
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager
import java.util.*

internal object WorldBorderCommand {
    private val CENTER_NO_CHANGE = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.center.failed")
    )
    private val SIZE_NO_CHANGE = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.set.failed.nochange")
    )
    private val SIZE_TOO_SMALL = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.set.failed.small.")
    )
    private val SIZE_TOO_BIG = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.set.failed.big.")
    )
    private val WARNING_TIME_NO_CHANGE = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.warning.time.failed")
    )
    private val WARNING_DISTANCE_NO_CHANGE = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.warning.distance.failed")
    )
    private val DAMAGE_BUFFER_NO_CHANGE = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.damage.buffer.failed")
    )
    private val DAMAGE_AMOUNT_NO_CHANGE = SimpleCommandExceptionType(
        TranslationTextComponent("commands.worldborder.damage.amount.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.worldborder + "worldborder"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["worldborder"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/worldborder` vanilla command")
        NativeCommandUtils.removeCommand("worldborder")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "distance", FloatArgumentType.floatArg(-6.0E7f, 6.0E7f)
                        ).executes { p_198908_0_ ->
                            setSize(
                                p_198908_0_.source,
                                p_198908_0_.source.world.worldBorder.diameter + FloatArgumentType.getFloat(
                                    p_198908_0_,
                                    "distance"
                                ).toDouble(),
                                0L
                            )
                        }.then(
                            Commands.argument(
                                "time", IntegerArgumentType.integer(0)
                            ).executes { p_198901_0_ ->
                                setSize(
                                    p_198901_0_.source,
                                    p_198901_0_.source.world.worldBorder.diameter + FloatArgumentType.getFloat(
                                        p_198901_0_,
                                        "distance"
                                    ).toDouble(),
                                    p_198901_0_.source.world.worldBorder.timeUntilTarget + IntegerArgumentType.getInteger(
                                        p_198901_0_,
                                        "time"
                                    ).toLong() * 1000L
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("set").then(
                        Commands.argument(
                            "distance", FloatArgumentType.floatArg(-6.0E7f, 6.0E7f)
                        ).executes { p_198906_0_ ->
                            setSize(
                                p_198906_0_.source,
                                FloatArgumentType.getFloat(p_198906_0_, "distance").toDouble(),
                                0L
                            )
                        }.then(
                            Commands.argument(
                                "time", IntegerArgumentType.integer(0)
                            ).executes { p_198909_0_ ->
                                setSize(
                                    p_198909_0_.source,
                                    FloatArgumentType.getFloat(p_198909_0_, "distance").toDouble(),
                                    IntegerArgumentType.getInteger(
                                        p_198909_0_,
                                        "time"
                                    ).toLong() * 1000L
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("center").then(
                        Commands.argument(
                            "pos", Vec2Argument.vec2()
                        ).executes { p_198893_0_ ->
                            setCenter(
                                p_198893_0_.source,
                                Vec2Argument.getVec2f(p_198893_0_, "pos")
                            )
                        }
                    )
                ).then(
                    Commands.literal("damage").then(
                        Commands.literal("amount").then(
                            Commands.argument(
                                "damagePerBlock", FloatArgumentType.floatArg(0.0f)
                            ).executes { p_198897_0_ ->
                                setDamageAmount(
                                    p_198897_0_.source,
                                    FloatArgumentType.getFloat(p_198897_0_, "damagePerBlock")
                                )
                            }
                        )
                    ).then(
                        Commands.literal("buffer").then(
                            Commands.argument(
                                "distance", FloatArgumentType.floatArg(0.0f)
                            ).executes { p_198905_0_ ->
                                setDamageBuffer(
                                    p_198905_0_.source,
                                    FloatArgumentType.getFloat(p_198905_0_, "distance")
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("get").executes { p_198900_0_ ->
                        getSize(p_198900_0_.source)
                    }
                ).then(
                    Commands.literal("warning").then(
                        Commands.literal("distance").then(
                            Commands.argument(
                                "distance", IntegerArgumentType.integer(0)
                            ).executes { p_198892_0_ ->
                                setWarningDistance(
                                    p_198892_0_.source,
                                    IntegerArgumentType.getInteger(p_198892_0_, "distance")
                                )
                            }
                        )
                    ).then(
                        Commands.literal("time").then(
                            Commands.argument(
                                "time", IntegerArgumentType.integer(0)
                            ).executes { p_198907_0_ ->
                                setWarningTime(
                                    p_198907_0_.source,
                                    IntegerArgumentType.getInteger(p_198907_0_, "time")
                                )
                            }
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.worldborder", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "worldborder")
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
                                "native.worldborder", "2"
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
    private fun setDamageBuffer(source: CommandSource, distance: Float): Int {
        checkPermissions(source)

        val worldborder = source.world.worldBorder
        return if (worldborder.damageBuffer == distance.toDouble()) {
            throw DAMAGE_BUFFER_NO_CHANGE.create()
        } else {
            worldborder.damageBuffer = distance.toDouble()
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.worldborder.damage.buffer.success", String.format(
                        Locale.ROOT, "%.2f", distance
                    )
                ), true
            )
            distance.toInt()
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setDamageAmount(source: CommandSource, damagePerBlock: Float): Int {
        checkPermissions(source)

        val worldborder = source.world.worldBorder
        return if (worldborder.damagePerBlock == damagePerBlock.toDouble()) {
            throw DAMAGE_AMOUNT_NO_CHANGE.create()
        } else {
            worldborder.damagePerBlock = damagePerBlock.toDouble()
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.worldborder.damage.amount.success", String.format(
                        Locale.ROOT, "%.2f", damagePerBlock
                    )
                ), true
            )
            damagePerBlock.toInt()
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setWarningTime(source: CommandSource, time: Int): Int {
        checkPermissions(source)

        val worldborder = source.world.worldBorder
        return if (worldborder.warningTime == time) {
            throw WARNING_TIME_NO_CHANGE.create()
        } else {
            worldborder.warningTime = time
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.worldborder.warning.time.success",
                    time
                ), true
            )
            time
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setWarningDistance(source: CommandSource, distance: Int): Int {
        checkPermissions(source)

        val worldborder = source.world.worldBorder
        return if (worldborder.warningDistance == distance) {
            throw WARNING_DISTANCE_NO_CHANGE.create()
        } else {
            worldborder.warningDistance = distance
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.worldborder.warning.distance.success",
                    distance
                ), true
            )
            distance
        }
    }

    private fun getSize(source: CommandSource): Int {
        checkPermissions(source)

        val d0 = source.world.worldBorder.diameter
        source.sendFeedback(
            TranslationTextComponent(
                "commands.worldborder.get", String.format(
                    Locale.ROOT, "%.0f", d0
                )
            ), false
        )
        return MathHelper.floor(d0 + 0.5)
    }

    @Throws(CommandSyntaxException::class)
    private fun setCenter(source: CommandSource, pos: Vec2f): Int {
        checkPermissions(source)

        val worldborder = source.world.worldBorder
        return if (
            worldborder.centerX == pos.x.toDouble() &&
            worldborder.centerZ == pos.y.toDouble()
        ) {
            throw CENTER_NO_CHANGE.create()
        } else {
            worldborder.setCenter(pos.x.toDouble(), pos.y.toDouble())
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.worldborder.center.success", String.format(
                        Locale.ROOT, "%.2f", pos.x
                    ), String.format("%.2f", pos.y)
                ), true
            )
            0
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setSize(source: CommandSource, newSize: Double, time: Long): Int {
        checkPermissions(source)

        val worldborder = source.world.worldBorder
        val d0 = worldborder.diameter
        return if (d0 == newSize) {
            throw SIZE_NO_CHANGE.create()
        } else if (newSize < 1.0) {
            throw SIZE_TOO_SMALL.create()
        } else if (newSize > 6.0E7) {
            throw SIZE_TOO_BIG.create()
        } else {
            if (time > 0L) {
                worldborder.setTransition(d0, newSize, time)
                if (newSize > d0) {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.worldborder.set.grow", String.format(
                                Locale.ROOT, "%.1f", newSize
                            ), (time / 1000L).toString()
                        ), true
                    )
                } else {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.worldborder.set.shrink", String.format(
                                Locale.ROOT, "%.1f", newSize
                            ), (time / 1000L).toString()
                        ), true
                    )
                }
            } else {
                worldborder.setTransition(newSize)
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.worldborder.set.immediate", String.format(
                            Locale.ROOT, "%.1f", newSize
                        )
                    ), true
                )
            }
            (newSize - d0).toInt()
        }
    }
}
