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
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.PotionArgument
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.potion.Effect
import net.minecraft.potion.EffectInstance
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object EffectCommand {
    private val GIVE_FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.effect.give.failed")
    )
    private val CLEAR_EVERYTHING_FAILED_EXCEPTION =
        SimpleCommandExceptionType(
            TranslationTextComponent("commands.effect.clear.everything.failed")
        )
    private val CLEAR_SPECIFIC_FAILED_EXCEPTION =
        SimpleCommandExceptionType(
            TranslationTextComponent("commands.effect.clear.specific.failed")
        )

    private val logger = LogManager.getLogger()
    private var aliases = CommandsConfigurationUtils.getConfig().aliases.effect + "effect"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["effect"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        NativeCommandUtils.removeCommand("effect")
        logger.info("Replacing `/effect` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("clear").then(
                        Commands.argument(
                            "targets", EntityArgument.entities()
                        ).executes { p_198352_0_ ->
                            clearAllEffects(
                                p_198352_0_.source,
                                EntityArgument.getEntities(p_198352_0_, "targets")
                            )
                        }.then(
                            Commands.argument(
                                "effect", PotionArgument.mobEffect()
                            ).executes { p_198356_0_ ->
                                clearEffect(
                                    p_198356_0_.source,
                                    EntityArgument.getEntities(p_198356_0_, "targets"),
                                    PotionArgument.getMobEffect(p_198356_0_, "effect")
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("give").then(
                        Commands.argument(
                            "targets", EntityArgument.entities()
                        ).then(
                            Commands.argument(
                                "effect", PotionArgument.mobEffect()
                            ).executes { p_198351_0_ ->
                                addEffect(
                                    p_198351_0_.source,
                                    EntityArgument.getEntities(p_198351_0_, "targets"),
                                    PotionArgument.getMobEffect(p_198351_0_, "effect"),
                                    null as Int?,
                                    0,
                                    true
                                )
                            }.then(
                                Commands.argument(
                                    "seconds", IntegerArgumentType.integer(1, 1000000)
                                ).executes { p_198357_0_ ->
                                    addEffect(
                                        p_198357_0_.source,
                                        EntityArgument.getEntities(p_198357_0_, "targets"),
                                        PotionArgument.getMobEffect(p_198357_0_, "effect"),
                                        IntegerArgumentType.getInteger(p_198357_0_, "seconds"),
                                        0,
                                        true
                                    )
                                }.then(
                                    Commands.argument(
                                        "amplifier", IntegerArgumentType.integer(0, 255)
                                    ).executes { p_198350_0_ ->
                                        addEffect(
                                            p_198350_0_.source,
                                            EntityArgument.getEntities(p_198350_0_, "targets"),
                                            PotionArgument.getMobEffect(p_198350_0_, "effect"),
                                            IntegerArgumentType.getInteger(p_198350_0_, "seconds"),
                                            IntegerArgumentType.getInteger(
                                                p_198350_0_, "amplifier"
                                            ),
                                            true
                                        )
                                    }.then(
                                        Commands.argument(
                                            "hideParticles", BoolArgumentType.bool()
                                        ).executes { p_198358_0_ ->
                                            addEffect(
                                                p_198358_0_.source,
                                                EntityArgument.getEntities(p_198358_0_, "targets"),
                                                PotionArgument.getMobEffect(p_198358_0_, "effect"),
                                                IntegerArgumentType.getInteger(
                                                    p_198358_0_, "seconds"
                                                ),
                                                IntegerArgumentType.getInteger(
                                                    p_198358_0_, "amplifier"
                                                ),
                                                !BoolArgumentType.getBool(
                                                    p_198358_0_, "hideParticles"
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
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.effect", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "effect")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.effect", "2"
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
    private fun addEffect(
        source: CommandSource,
        targets: Collection<Entity>,
        effect: Effect,
        seconds: Int?,
        amplifier: Int,
        showParticles: Boolean
    ): Int {
        checkPermissions(source)

        var i = 0
        val j: Int = if (seconds != null) {
            if (effect.isInstant) {
                seconds
            } else {
                seconds * 20
            }
        } else if (effect.isInstant) {
            1
        } else {
            600
        }
        for (entity in targets) {
            if (entity is LivingEntity) {
                val effectinstance = EffectInstance(
                    effect, j, amplifier, false, showParticles
                )
                if (entity.addPotionEffect(effectinstance)) {
                    ++i
                }
            }
        }
        return if (i == 0) {
            throw GIVE_FAILED_EXCEPTION.create()
        } else {
            if (targets.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.effect.give.success.single",
                        effect.displayName,
                        targets.iterator().next().displayName,
                        j / 20
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.effect.give.success.multiple",
                        effect.displayName,
                        targets.size,
                        j / 20
                    ), true
                )
            }
            i
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun clearAllEffects(
        source: CommandSource,
        targets: Collection<Entity>
    ): Int {
        checkPermissions(source)

        var i = 0
        for (entity in targets) {
            if (entity is LivingEntity && entity.clearActivePotions()) {
                ++i
            }
        }
        return if (i == 0) {
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create()
        } else {
            if (targets.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.effect.clear.everything.success.single",
                        targets.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.effect.clear.everything.success.multiple",
                        targets.size
                    ), true
                )
            }
            i
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun clearEffect(
        source: CommandSource,
        targets: Collection<Entity>,
        effect: Effect
    ): Int {
        checkPermissions(source)

        var i = 0
        for (entity in targets) {
            if (entity is LivingEntity && entity.removePotionEffect(effect)) {
                ++i
            }
        }
        return if (i == 0) {
            throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create()
        } else {
            if (targets.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.effect.clear.specific.success.single",
                        effect.displayName,
                        targets.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.effect.clear.specific.success.multiple",
                        effect.displayName,
                        targets.size
                    ), true
                )
            }
            i
        }
    }
}

