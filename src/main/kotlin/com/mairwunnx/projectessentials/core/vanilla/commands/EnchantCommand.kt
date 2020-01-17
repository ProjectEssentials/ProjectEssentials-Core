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
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EnchantmentArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.util.function.Function

object EnchantCommand {
    private val NONLIVING_ENTITY_EXCEPTION =
        DynamicCommandExceptionType(
            Function { p_208839_0_: Any? ->
                TranslationTextComponent(
                    "commands.enchant.failed.entity", p_208839_0_
                )
            }
        )
    private val ITEMLESS_EXCEPTION = DynamicCommandExceptionType(
        Function { p_208835_0_: Any? ->
            TranslationTextComponent(
                "commands.enchant.failed.itemless", p_208835_0_
            )
        }
    )
    private val INCOMPATIBLE_ENCHANTS_EXCEPTION =
        DynamicCommandExceptionType(
            Function { p_208837_0_: Any? ->
                TranslationTextComponent(
                    "commands.enchant.failed.incompatible", p_208837_0_
                )
            }
        )
    private val INVALID_LEVEL = Dynamic2CommandExceptionType(
        Dynamic2CommandExceptionType.Function { p_208840_0_: Any?, p_208840_1_: Any? ->
            TranslationTextComponent(
                "commands.enchant.failed.level", p_208840_0_, p_208840_1_
            )
        }
    )
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.enchant.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.enchant + "enchant"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["enchant"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/enchant` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument("targets", EntityArgument.entities()).then(
                        Commands.argument(
                            "enchantment", EnchantmentArgument.enchantment()
                        ).executes { p_202648_0_ ->
                            enchant(
                                p_202648_0_.source,
                                EntityArgument.getEntities(p_202648_0_, "targets"),
                                EnchantmentArgument.getEnchantment(p_202648_0_, "enchantment"),
                                1
                            )
                        }.then(
                            Commands.argument(
                                "level", IntegerArgumentType.integer(0)
                            ).executes { p_202650_0_ ->
                                enchant(
                                    p_202650_0_.source,
                                    EntityArgument.getEntities(p_202650_0_, "targets"),
                                    EnchantmentArgument.getEnchantment(p_202650_0_, "enchantment"),
                                    IntegerArgumentType.getInteger(p_202650_0_, "level")
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
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.enchant", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "enchant")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.enchant.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun enchant(
        source: CommandSource,
        targets: Collection<Entity>,
        enchantmentIn: Enchantment,
        level: Int
    ): Int {
        checkPermissions(source)

        return if (
            !CommandsConfigurationUtils.getConfig().disableSafelyEnchantLevel &&
            level > enchantmentIn.maxLevel
        ) {
            throw INVALID_LEVEL.create(level, enchantmentIn.maxLevel)
        } else {
            var i = 0
            for (entity in targets) {
                if (entity is LivingEntity) {
                    val itemstack = entity.heldItemMainhand
                    if (!itemstack.isEmpty) {
                        if (enchantmentIn.canApply(itemstack) &&
                            EnchantmentHelper.areAllCompatibleWith(
                                EnchantmentHelper.getEnchantments(itemstack).keys, enchantmentIn
                            )
                        ) {
                            itemstack.addEnchantment(enchantmentIn, level)
                            ++i
                        } else if (targets.size == 1) {
                            throw INCOMPATIBLE_ENCHANTS_EXCEPTION.create(
                                itemstack.item.getDisplayName(itemstack).string
                            )
                        }
                    } else if (targets.size == 1) {
                        throw ITEMLESS_EXCEPTION.create(entity.name.string)
                    }
                } else if (targets.size == 1) {
                    throw NONLIVING_ENTITY_EXCEPTION.create(entity.name.string)
                }
            }
            if (i == 0) {
                throw FAILED_EXCEPTION.create()
            } else {
                if (targets.size == 1) {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.enchant.success.single",
                            enchantmentIn.getDisplayName(level),
                            targets.iterator().next().displayName
                        ), true
                    )
                } else {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.enchant.success.multiple",
                            enchantmentIn.getDisplayName(level),
                            targets.size
                        ), true
                    )
                }
                i
            }
        }
    }
}
