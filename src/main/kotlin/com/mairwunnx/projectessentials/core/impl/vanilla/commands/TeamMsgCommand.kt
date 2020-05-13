package com.mairwunnx.projectessentials.core.impl.vanilla.commands


import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
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
import java.util.function.Consumer

internal object TeamMsgCommand : VanillaCommandBase() {
    private val field_218919_a = SimpleCommandExceptionType(
        TranslationTextComponent("commands.teammsg.failed.noteam")
    )

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["teammsg"] = mutableListOf("tm")
    }

    fun register(p_218915_0_: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("teammsg")
        CommandAPI.removeCommand("tm")
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
            if (!hasPermission(source.asPlayer(), "native.messaging.teammsg", 0)) {
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
                                "native.messaging.teammsg", "0"
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
