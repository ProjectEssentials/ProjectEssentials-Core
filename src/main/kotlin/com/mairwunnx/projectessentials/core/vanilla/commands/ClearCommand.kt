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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ItemPredicateArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.util.function.Function
import java.util.function.Predicate

object ClearCommand {
    private val SINGLE_FAILED_EXCEPTION = DynamicCommandExceptionType(
        Function { p_208785_0_: Any? ->
            TranslationTextComponent(
                "clear.failed.single",
                p_208785_0_
            )
        }
    )
    private val MULTIPLE_FAILED_EXCEPTION =
        DynamicCommandExceptionType(
            Function { p_208787_0_: Any? ->
                TranslationTextComponent(
                    "clear.failed.multiple",
                    p_208787_0_
                )
            }
        )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.clear + "clear"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["clear"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/clear` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198241_0_ ->
                    clearInventory(
                        p_198241_0_.source,
                        setOf(p_198241_0_.source.asPlayer()),
                        Predicate { true },
                        -1
                    )
                }.then(
                    Commands.argument(
                        "targets",
                        EntityArgument.players()
                    ).executes { p_198245_0_ ->
                        clearInventory(
                            p_198245_0_.source,
                            EntityArgument.getPlayers(p_198245_0_, "targets"),
                            Predicate { true },
                            -1
                        )
                    }.then(
                        Commands.argument(
                            "item",
                            ItemPredicateArgument.itemPredicate()
                        ).executes { p_198240_0_ ->
                            clearInventory(
                                p_198240_0_.source,
                                EntityArgument.getPlayers(p_198240_0_, "targets"),
                                ItemPredicateArgument.getItemPredicate(p_198240_0_, "item"),
                                -1
                            )
                        }.then(
                            Commands.argument(
                                "maxCount",
                                IntegerArgumentType.integer(0)
                            ).executes { p_198246_0_ ->
                                clearInventory(
                                    p_198246_0_.source,
                                    EntityArgument.getPlayers(p_198246_0_, "targets"),
                                    ItemPredicateArgument.getItemPredicate(p_198246_0_, "item"),
                                    IntegerArgumentType.getInteger(p_198246_0_, "maxCount")
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
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.clear", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "clear")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.clear.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun clearInventory(
        source: CommandSource,
        targetPlayers: Collection<ServerPlayerEntity>,
        itemPredicateIn: Predicate<ItemStack>,
        maxCount: Int
    ): Int {
        checkPermissions(source)

        var i = 0
        for (serverplayerentity in targetPlayers) {
            i += serverplayerentity.inventory.clearMatchingItems(itemPredicateIn, maxCount)
            serverplayerentity.openContainer.detectAndSendChanges()
            serverplayerentity.updateHeldItem()
        }
        return if (i == 0) {
            if (targetPlayers.size == 1) {
                throw SINGLE_FAILED_EXCEPTION.create(
                    targetPlayers.iterator().next().name.formattedText
                )
            } else {
                throw MULTIPLE_FAILED_EXCEPTION.create(targetPlayers.size)
            }
        } else {
            if (maxCount == 0) {
                if (targetPlayers.size == 1) {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.clear.test.single",
                            i,
                            targetPlayers.iterator().next().displayName
                        ), true
                    )
                } else {
                    source.sendFeedback(
                        TranslationTextComponent(
                            "commands.clear.test.multiple",
                            i,
                            targetPlayers.size
                        ), true
                    )
                }
            } else if (targetPlayers.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.clear.success.single",
                        i,
                        targetPlayers.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.clear.success.multiple",
                        i,
                        targetPlayers.size
                    ), true
                )
            }
            i
        }
    }
}

