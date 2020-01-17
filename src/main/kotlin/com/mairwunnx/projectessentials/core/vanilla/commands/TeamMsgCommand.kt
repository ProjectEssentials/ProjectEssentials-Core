package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.MessageArgument
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer

internal object TeamMsgCommand {
    private val field_218919_a = SimpleCommandExceptionType(
        TranslationTextComponent("commands.teammsg.failed.noteam")
    )
    private val logger = LogManager.getLogger()

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["teammsg"] = mutableListOf("tm")
    }

    fun register(p_218915_0_: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/teammsg` vanilla command")
        tryAssignAliases()

        val literalcommandnode = p_218915_0_.register(
            Commands.literal("teammsg").then(
                Commands.argument(
                    "message", MessageArgument.message()
                ).executes { p_218916_0_ ->
                    func_218917_a(
                        p_218916_0_.source,
                        MessageArgument.getMessage(p_218916_0_, "message")
                    )
                }
            )
        )
        p_218915_0_.register(Commands.literal("tm").redirect(literalcommandnode))
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.teammsg", 0)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "teammsg")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.teammsg.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun func_218917_a(p_218917_0_: CommandSource, p_218917_1_: ITextComponent): Int {
        checkPermissions(p_218917_0_)
        val entity = p_218917_0_.assertIsEntity()
        val scoreplayerteam = entity.team as ScorePlayerTeam?
        return if (scoreplayerteam == null) {
            throw field_218919_a.create()
        } else {
            val consumer =
                Consumer { p_218918_0_: Style ->
                    p_218918_0_.setHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            TranslationTextComponent("chat.type.team.hover")
                        )
                    ).clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg ")
                }
            val itextcomponent = scoreplayerteam.commandName.applyTextStyle(consumer)
            for (itextcomponent1 in itextcomponent.siblings) {
                itextcomponent1.applyTextStyle(consumer)
            }
            val list = p_218917_0_.server.playerList.players
            for (serverplayerentity in list) {
                if (serverplayerentity === entity) {
                    serverplayerentity.sendMessage(
                        TranslationTextComponent(
                            "chat.type.team.sent",
                            itextcomponent,
                            p_218917_0_.displayName,
                            p_218917_1_.deepCopy()
                        )
                    )
                } else if (serverplayerentity.team === scoreplayerteam) {
                    serverplayerentity.sendMessage(
                        TranslationTextComponent(
                            "chat.type.team.text",
                            itextcomponent,
                            p_218917_0_.displayName,
                            p_218917_1_.deepCopy()
                        )
                    )
                }
            }
            list.size
        }
    }
}
