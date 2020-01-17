/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.ClickEvent
import org.apache.logging.log4j.LogManager

object SeedCommand {
    private val logger = LogManager.getLogger()

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/seed` vanilla command")

        dispatcher.register(
            Commands.literal("seed").executes { p_198672_0_ ->
                checkPermissions(p_198672_0_.source)
                val i = p_198672_0_.source.world.seed
                val itextcomponent = TextComponentUtils.wrapInSquareBrackets(
                    StringTextComponent(i.toString()).applyTextStyle { p_211752_2_ ->
                        p_211752_2_.setColor(TextFormatting.GREEN).setClickEvent(
                            ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                i.toString()
                            )
                        ).insertion = i.toString()
                    }
                )
                p_198672_0_.source.sendFeedback(
                    TranslationTextComponent(
                        "commands.seed.success",
                        itextcomponent
                    ), false
                )
                i.toInt()
            }
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.seed", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "seed")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.seed.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
