/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object SpawnPointCommand : VanillaCommandBase() {
    private var aliases =
        configuration.take().aliases.spawnpoint + "spawnpoint"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandsAliases.aliases["spawnpoint"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("spawnpoint")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198697_0_ ->
                    setSpawnPoint(
                        p_198697_0_.source,
                        setOf(p_198697_0_.source.asPlayer()),
                        BlockPos(p_198697_0_.source.pos)
                    )
                }.then(
                    Commands.argument(
                        "targets", EntityArgument.players()
                    ).executes { p_198694_0_ ->
                        setSpawnPoint(
                            p_198694_0_.source,
                            EntityArgument.getPlayers(p_198694_0_, "targets"),
                            BlockPos(p_198694_0_.source.pos)
                        )
                    }.then(
                        Commands.argument(
                            "pos", BlockPosArgument.blockPos()
                        ).executes { p_198698_0_ ->
                            setSpawnPoint(
                                p_198698_0_.source,
                                EntityArgument.getPlayers(p_198698_0_, "targets"),
                                BlockPosArgument.getBlockPos(p_198698_0_, "pos")
                            )
                        }
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.spawnpoint", 2)) {
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
                                "native.spawnpoint", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    private fun setSpawnPoint(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        pos: BlockPos
    ): Int {
        checkPermissions(source)

        for (serverplayerentity in targets) {
            @Suppress("DEPRECATION")
            serverplayerentity.setSpawnPoint(pos, true)
        }
        if (targets.size == 1) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.spawnpoint.success.single",
                    pos.x, pos.y, pos.z,
                    targets.iterator().next().displayName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.spawnpoint.success.multiple",
                    pos.x, pos.y, pos.z,
                    targets.size
                ), true
            )
        }
        return targets.size
    }
}

