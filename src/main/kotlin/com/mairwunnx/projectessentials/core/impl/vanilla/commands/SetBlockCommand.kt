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

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.command.arguments.BlockStateArgument
import net.minecraft.command.arguments.BlockStateInput
import net.minecraft.inventory.IClearable
import net.minecraft.util.CachedBlockInfo
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.Predicate

internal object SetBlockCommand : VanillaCommandBase() {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.setblock.failed")
    )

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("setblock")

        dispatcher.register(
            Commands.literal("setblock").then(
                Commands.argument(
                    "pos", BlockPosArgument.blockPos()
                ).then(
                    Commands.argument(
                        "block", BlockStateArgument.blockState()
                    ).executes { p_198682_0_: CommandContext<CommandSource> ->
                        setBlock(
                            p_198682_0_.source,
                            BlockPosArgument.getLoadedBlockPos(p_198682_0_, "pos"),
                            BlockStateArgument.getBlockState(p_198682_0_, "block"),
                            Mode.REPLACE,
                            null as Predicate<CachedBlockInfo>?
                        )
                    }.then(
                        Commands.literal("destroy").executes { p_198685_0_ ->
                            setBlock(
                                p_198685_0_.source,
                                BlockPosArgument.getLoadedBlockPos(p_198685_0_, "pos"),
                                BlockStateArgument.getBlockState(p_198685_0_, "block"),
                                Mode.DESTROY,
                                null as Predicate<CachedBlockInfo>?
                            )
                        }
                    ).then(
                        Commands.literal("keep").executes { p_198681_0_ ->
                            setBlock(
                                p_198681_0_.source,
                                BlockPosArgument.getLoadedBlockPos(p_198681_0_, "pos"),
                                BlockStateArgument.getBlockState(p_198681_0_, "block"),
                                Mode.REPLACE,
                                Predicate { p_198687_0_: CachedBlockInfo ->
                                    p_198687_0_.world.isAirBlock(p_198687_0_.pos)
                                }
                            )
                        }
                    ).then(
                        Commands.literal("replace").executes { p_198686_0_ ->
                            setBlock(
                                p_198686_0_.source,
                                BlockPosArgument.getLoadedBlockPos(p_198686_0_, "pos"),
                                BlockStateArgument.getBlockState(p_198686_0_, "block"),
                                Mode.REPLACE,
                                null as Predicate<CachedBlockInfo>?
                            )
                        }
                    )
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.setblock", 2)) {
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
                                "native.setblock", "2"
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
    private fun setBlock(
        source: CommandSource,
        pos: BlockPos,
        state: BlockStateInput,
        mode: Mode,
        predicate: Predicate<CachedBlockInfo>?
    ): Int {
        checkPermissions(source)

        val serverworld = source.world
        return if (predicate != null && !predicate.test(
                CachedBlockInfo(serverworld, pos, true)
            )
        ) {
            throw FAILED_EXCEPTION.create()
        } else {
            val flag: Boolean
            flag = if (mode == Mode.DESTROY) {
                serverworld.destroyBlock(pos, true)
                @Suppress("DEPRECATION")
                !state.state.isAir
            } else {
                val tileentity = serverworld.getTileEntity(pos)
                IClearable.clearObj(tileentity)
                true
            }
            if (flag && !state.place(serverworld, pos, 2)) {
                throw FAILED_EXCEPTION.create()
            } else {
                serverworld.notifyNeighbors(pos, state.state.block)
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.setblock.success",
                        pos.x, pos.y, pos.z
                    ), true
                )
                1
            }
        }
    }

    enum class Mode {
        REPLACE, DESTROY
    }
}
