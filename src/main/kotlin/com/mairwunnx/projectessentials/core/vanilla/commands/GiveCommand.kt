/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

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
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ItemArgument
import net.minecraft.command.arguments.ItemInput
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager
import kotlin.math.min

internal object GiveCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.give + "give"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["give"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/give` vanilla command")
        NativeCommandUtils.removeCommand("give")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "targets", EntityArgument.players()
                    ).then(
                        Commands.argument(
                            "item", ItemArgument.item()
                        ).executes { p_198493_0_ ->
                            giveItem(
                                p_198493_0_.source,
                                ItemArgument.getItem(p_198493_0_, "item"),
                                EntityArgument.getPlayers(p_198493_0_, "targets"),
                                1
                            )
                        }.then(
                            Commands.argument(
                                "count", IntegerArgumentType.integer(1)
                            ).executes { p_198495_0_ ->
                                giveItem(
                                    p_198495_0_.source,
                                    ItemArgument.getItem(p_198495_0_, "item"),
                                    EntityArgument.getPlayers(p_198495_0_, "targets"),
                                    IntegerArgumentType.getInteger(p_198495_0_, "count")
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
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.give", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "give")
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
                                "native.give", "2"
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
    private fun giveItem(
        source: CommandSource,
        itemIn: ItemInput,
        targets: Collection<ServerPlayerEntity>,
        count: Int
    ): Int {
        checkPermissions(source)

        for (serverplayerentity in targets) {
            var i = count
            while (i > 0) {
                @Suppress("DEPRECATION") val j = min(itemIn.item.maxStackSize, i)
                i -= j
                val itemstack = itemIn.createStack(j, false)
                val flag = serverplayerentity.inventory.addItemStackToInventory(itemstack)
                if (flag && itemstack.isEmpty) {
                    itemstack.count = 1
                    val itementity1 = serverplayerentity.dropItem(itemstack, false)
                    itementity1?.makeFakeItem()
                    serverplayerentity.world.playSound(
                        null as PlayerEntity?,
                        serverplayerentity.func_226277_ct_(),
                        serverplayerentity.func_226278_cu_(),
                        serverplayerentity.func_226281_cx_(),
                        SoundEvents.ENTITY_ITEM_PICKUP,
                        SoundCategory.PLAYERS,
                        0.2f,
                        ((serverplayerentity.rng.nextFloat() - serverplayerentity.rng.nextFloat()) * 0.7f + 1.0f) * 2.0f
                    )
                    serverplayerentity.container.detectAndSendChanges()
                } else {
                    val itementity = serverplayerentity.dropItem(itemstack, false)
                    if (itementity != null) {
                        itementity.setNoPickupDelay()
                        itementity.ownerId = serverplayerentity.uniqueID
                    }
                }
            }
        }

        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.give.success.single",
                    count,
                    itemIn.createStack(count, false).textComponent,
                    targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.give.success.single",
                    count,
                    itemIn.createStack(count, false).textComponent,
                    targets.size
                ), true
            )
        }
        return targets.size
    }
}

