/**
 * ! This command implementation by Mojang Studios!
 *
 * Decompiled with idea source code was converted to kotlin code.
 * But with additions such as permissions checking and etc.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */
package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.core.api.v1.SETTING_DISABLE_SAFE_ENCHANT
import com.mairwunnx.projectessentials.core.impl.generalConfiguration
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EnchantmentArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper.areAllCompatibleWith
import net.minecraft.enchantment.EnchantmentHelper.getEnchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import java.util.function.Function
import net.minecraft.util.text.TranslationTextComponent as textComponentOf

internal object EnchantCommand : VanillaCommandBase("enchant") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        super.register(dispatcher).also {
            dispatcher.register(
                Commands.literal(name).requires {
                    isAllowed(it, "enchant", 2)
                }.then(
                    Commands.argument("targets", EntityArgument.entities()).requires {
                        isAllowed(it, "enchant.other", 3)
                    }.then(
                        Commands.argument(
                            "enchantment", EnchantmentArgument.enchantment()
                        ).executes {
                            enchant(
                                it.source, EntityArgument.getEntities(it, "targets"),
                                EnchantmentArgument.getEnchantment(it, "enchantment"), 1
                            )
                        }.then(
                            Commands.argument("level", IntegerArgumentType.integer(0)).executes {
                                enchant(
                                    it.source,
                                    EntityArgument.getEntities(it, "targets"),
                                    EnchantmentArgument.getEnchantment(it, "enchantment"),
                                    IntegerArgumentType.getInteger(it, "level")
                                )
                            }
                        )
                    )
                )
            )
        }
    }

    private fun enchant(
        source: CommandSource, targets: Collection<Entity>, enchantmentIn: Enchantment, level: Int
    ): Int {
        if (
            level > enchantmentIn.maxLevel &&
            !generalConfiguration.getBool(SETTING_DISABLE_SAFE_ENCHANT)
        ) throw invalidLevel.create(level, enchantmentIn.maxLevel)

        var counter = 0
        targets.forEach { entity ->
            if (entity is LivingEntity) {
                val iStack = entity.heldItemMainhand
                if (!iStack.isEmpty) {
                    if (enchantmentIn.canApply(iStack) && validate(iStack, enchantmentIn)) {
                        iStack.addEnchantment(enchantmentIn, level).let { ++counter }
                    } else if (targets.size == 1) {
                        throw incompatibleEnchantsException.create(iStack.item.getDisplayName(iStack).string)
                    }
                } else if (targets.size == 1) throw itemlessException.create(entity.name.string)
            } else if (targets.size == 1) throw nonLivingEntityException.create(entity.name.string)
        }
        if (counter == 0) throw failedException.create() else {
            if (targets.size == 1) {
                feedback(
                    source, "single", enchantmentIn.getDisplayName(level),
                    targets.iterator().next().displayName
                )
            } else feedback(source, "multiple", enchantmentIn.getDisplayName(level), targets.size)
        }
        return counter
    }

    private fun feedback(source: CommandSource, mode: String, vararg args: Any) =
        source.sendFeedback(textComponentOf("commands.enchant.success.$mode", args), true)

    private fun validate(iStack: ItemStack, enchantmentIn: Enchantment) =
        areAllCompatibleWith(getEnchantments(iStack).keys, enchantmentIn)

    private val nonLivingEntityException = DynamicCommandExceptionType(
        Function { textComponentOf("commands.enchant.failed.entity", it) }
    )
    private val itemlessException = DynamicCommandExceptionType(
        Function { textComponentOf("commands.enchant.failed.itemless", it) }
    )
    private val incompatibleEnchantsException = DynamicCommandExceptionType(
        Function { textComponentOf("commands.enchant.failed.incompatible", it) }
    )
    private val invalidLevel = Dynamic2CommandExceptionType(
        Dynamic2CommandExceptionType.Function { p_208840_0_, p_208840_1_ ->
            textComponentOf("commands.enchant.failed.level", p_208840_0_, p_208840_1_)
        }
    )
    private val failedException = SimpleCommandExceptionType(
        textComponentOf("commands.enchant.failed")
    )
}
