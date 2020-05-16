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


import com.mairwunnx.projectessentials.core.api.v1.SETTING_DISABLE_SAFE_ENCHANT
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
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
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.Function

internal object EnchantCommand : VanillaCommandBase() {
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

    private var aliases =
        configuration.take().aliases.enchant + "enchant"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["enchant"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("enchant")
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
            if (!hasPermission(source.asPlayer(), "native.enchant", 2)) {
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
                                "native.enchant", "2"
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
    private fun enchant(
        source: CommandSource,
        targets: Collection<Entity>,
        enchantmentIn: Enchantment,
        level: Int
    ): Int {
        checkPermissions(source)

        return if (
            !generalConfiguration.getBoolOrDefault(SETTING_DISABLE_SAFE_ENCHANT, false) &&
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
