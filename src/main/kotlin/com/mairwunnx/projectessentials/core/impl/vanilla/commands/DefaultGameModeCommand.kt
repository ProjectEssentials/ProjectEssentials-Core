/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands


import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.GameType

internal object DefaultGameModeCommand : VanillaCommandBase() {

    private var aliases =
        configuration.take().aliases.defaultgamemode + "defaultgamemode"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["defaultgamemode"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("defaultgamemode")
        tryAssignAliases()

        var literalargumentbuilder: LiteralArgumentBuilder<CommandSource>

        aliases.forEach { command ->
            literalargumentbuilder = Commands.literal(command)

            for (gametype in GameType.values()) {
                if (gametype != GameType.NOT_SET) {
                    literalargumentbuilder.then(
                        Commands.literal(gametype.getName()).executes { p_198343_1_ ->
                            setGameType(p_198343_1_.source, gametype)
                        }
                    )
                }
            }

            dispatcher.register(literalargumentbuilder)
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.defaultgamemode", 2)) {

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
                                "native.defaultgamemode", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    /**
     * Set Gametype of player who ran the command
     */
    private fun setGameType(commandSourceIn: CommandSource, gamemode: GameType): Int {
        checkPermissions(commandSourceIn)

        var i = 0
        val minecraftserver = commandSourceIn.server
        minecraftserver.gameType = gamemode
        if (minecraftserver.forceGamemode) {
            for (serverplayerentity in minecraftserver.playerList.players) {
                if (serverplayerentity.interactionManager.gameType != gamemode) {
                    serverplayerentity.setGameType(gamemode)
                    ++i
                }
            }
        }
        commandSourceIn.sendFeedback(
            TranslationTextComponent(
                "commands.defaultgamemode.success",
                gamemode.displayName
            ), true
        )
        return i
    }
}
