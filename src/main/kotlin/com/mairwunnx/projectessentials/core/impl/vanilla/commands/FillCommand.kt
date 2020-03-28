/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands

import com.google.common.collect.Lists
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.block.Blocks
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.command.arguments.BlockPredicateArgument
import net.minecraft.command.arguments.BlockStateArgument
import net.minecraft.command.arguments.BlockStateInput
import net.minecraft.inventory.IClearable
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.CachedBlockInfo
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MutableBoundingBox
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.Predicate

internal object FillCommand : VanillaCommandBase() {
    private val TOO_BIG_EXCEPTION = Dynamic2CommandExceptionType(
        Dynamic2CommandExceptionType.Function { p_208897_0_: Any?, p_208897_1_: Any? ->
            TranslationTextComponent(
                "commands.fill.toobig", p_208897_0_, p_208897_1_
            )
        }
    )
    private val AIR = BlockStateInput(Blocks.AIR.defaultState, emptySet(), null as CompoundNBT?)
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.fill.failed")
    )

    private var aliases =
        configuration.take().aliases.fill + "fill"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandsAliases.aliases["fill"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("fill")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "from", BlockPosArgument.blockPos()
                    ).then(
                        Commands.argument(
                            "to", BlockPosArgument.blockPos()
                        ).then(
                            Commands.argument(
                                "block", BlockStateArgument.blockState()
                            ).executes { p_198472_0_ ->
                                doFill(
                                    p_198472_0_.source,
                                    MutableBoundingBox(
                                        BlockPosArgument.getLoadedBlockPos(p_198472_0_, "from"),
                                        BlockPosArgument.getLoadedBlockPos(p_198472_0_, "to")
                                    ),
                                    BlockStateArgument.getBlockState(p_198472_0_, "block"),
                                    Mode.REPLACE,
                                    null as Predicate<CachedBlockInfo>?
                                )
                            }.then(
                                Commands.literal("replace").executes { p_198464_0_ ->
                                    doFill(
                                        p_198464_0_.source,
                                        MutableBoundingBox(
                                            BlockPosArgument.getLoadedBlockPos(p_198464_0_, "from"),
                                            BlockPosArgument.getLoadedBlockPos(p_198464_0_, "to")
                                        ),
                                        BlockStateArgument.getBlockState(p_198464_0_, "block"),
                                        Mode.REPLACE,
                                        null as Predicate<CachedBlockInfo>?
                                    )
                                }.then(
                                    Commands.argument(
                                        "filter", BlockPredicateArgument.blockPredicate()
                                    ).executes { p_198466_0_ ->
                                        doFill(
                                            p_198466_0_.source,
                                            MutableBoundingBox(
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198466_0_, "from"
                                                ),
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198466_0_, "to"
                                                )
                                            ),
                                            BlockStateArgument.getBlockState(p_198466_0_, "block"),
                                            Mode.REPLACE,
                                            BlockPredicateArgument.getBlockPredicate(
                                                p_198466_0_, "filter"
                                            )
                                        )
                                    }
                                )
                            ).then(
                                Commands.literal("keep").executes { p_198462_0_ ->
                                    doFill(
                                        p_198462_0_.source,
                                        MutableBoundingBox(
                                            BlockPosArgument.getLoadedBlockPos(p_198462_0_, "from"),
                                            BlockPosArgument.getLoadedBlockPos(p_198462_0_, "to")
                                        ),
                                        BlockStateArgument.getBlockState(p_198462_0_, "block"),
                                        Mode.REPLACE,
                                        Predicate { p_198469_0_ ->
                                            p_198469_0_.world.isAirBlock(p_198469_0_.pos)
                                        }
                                    )
                                }
                            ).then(
                                Commands.literal("outline").executes { p_198467_0_ ->
                                    doFill(
                                        p_198467_0_.source,
                                        MutableBoundingBox(
                                            BlockPosArgument.getLoadedBlockPos(p_198467_0_, "from"),
                                            BlockPosArgument.getLoadedBlockPos(p_198467_0_, "to")
                                        ),
                                        BlockStateArgument.getBlockState(p_198467_0_, "block"),
                                        Mode.OUTLINE,
                                        null as Predicate<CachedBlockInfo>?
                                    )
                                }
                            ).then(
                                Commands.literal("hollow").executes { p_198461_0_ ->
                                    doFill(
                                        p_198461_0_.source,
                                        MutableBoundingBox(
                                            BlockPosArgument.getLoadedBlockPos(p_198461_0_, "from"),
                                            BlockPosArgument.getLoadedBlockPos(p_198461_0_, "to")
                                        ),
                                        BlockStateArgument.getBlockState(p_198461_0_, "block"),
                                        Mode.HOLLOW,
                                        null as Predicate<CachedBlockInfo>?
                                    )
                                }
                            ).then(
                                Commands.literal("destroy").executes { p_198468_0_ ->
                                    doFill(
                                        p_198468_0_.source,
                                        MutableBoundingBox(
                                            BlockPosArgument.getLoadedBlockPos(p_198468_0_, "from"),
                                            BlockPosArgument.getLoadedBlockPos(p_198468_0_, "to")
                                        ),
                                        BlockStateArgument.getBlockState(p_198468_0_, "block"),
                                        Mode.DESTROY,
                                        null as Predicate<CachedBlockInfo>?
                                    )
                                }
                            )
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.fill", 2)) {
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
                                "native.fill", "2"
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
    private fun doFill(
        source: CommandSource,
        area: MutableBoundingBox,
        newBlock: BlockStateInput,
        mode: Mode,
        replacingPredicate: Predicate<CachedBlockInfo>?
    ): Int {
        checkPermissions(source)

        val i = area.xSize * area.ySize * area.zSize
        return if (i > 32768) {
            throw TOO_BIG_EXCEPTION.create(32768, i)
        } else {
            val list: MutableList<BlockPos> = Lists.newArrayList()
            val serverworld = source.world
            var j = 0
            for (blockpos in BlockPos.getAllInBoxMutable(
                area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ
            )) {
                if (replacingPredicate == null || replacingPredicate.test(
                        CachedBlockInfo(serverworld, blockpos, true)
                    )
                ) {
                    val blockstateinput =
                        mode.filter.filter(area, blockpos, newBlock, serverworld)
                    if (blockstateinput != null) {
                        val tileentity = serverworld.getTileEntity(blockpos)
                        IClearable.clearObj(tileentity)
                        if (blockstateinput.place(serverworld, blockpos, 2)) {
                            list.add(blockpos.toImmutable())
                            ++j
                        }
                    }
                }
            }
            for (blockpos1 in list) {
                val block = serverworld.getBlockState(blockpos1).block
                serverworld.notifyNeighbors(blockpos1, block)
            }
            if (j == 0) {
                throw FAILED_EXCEPTION.create()
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.fill.success", j
                    ), true
                )
                j
            }
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    internal enum class Mode(val filter: net.minecraft.command.impl.SetBlockCommand.IFilter) {
        REPLACE(net.minecraft.command.impl.SetBlockCommand.IFilter { p_198450_0_, p_198450_1_, p_198450_2_, p_198450_3_ ->
            p_198450_2_
        }),
        OUTLINE(
            net.minecraft.command.impl.SetBlockCommand.IFilter { p_198454_0_, p_198454_1_, p_198454_2_, p_198454_3_ ->
                if (p_198454_1_.x != p_198454_0_.minX &&
                    p_198454_1_.x != p_198454_0_.maxX &&
                    p_198454_1_.y != p_198454_0_.minY &&
                    p_198454_1_.y != p_198454_0_.maxY &&
                    p_198454_1_.z != p_198454_0_.minZ &&
                    p_198454_1_.z != p_198454_0_.maxZ
                ) null else p_198454_2_
            }
        ),
        HOLLOW(
            net.minecraft.command.impl.SetBlockCommand.IFilter { p_198453_0_, p_198453_1_, p_198453_2_, p_198453_3_ ->
                if (p_198453_1_.x != p_198453_0_.minX &&
                    p_198453_1_.x != p_198453_0_.maxX &&
                    p_198453_1_.y != p_198453_0_.minY &&
                    p_198453_1_.y != p_198453_0_.maxY &&
                    p_198453_1_.z != p_198453_0_.minZ &&
                    p_198453_1_.z != p_198453_0_.maxZ
                ) AIR else p_198453_2_
            }
        ),
        DESTROY(
            net.minecraft.command.impl.SetBlockCommand.IFilter { p_198452_0_, p_198452_1_, p_198452_2_, p_198452_3_ ->
                p_198452_3_.destroyBlock(p_198452_1_, true)
                p_198452_2_
            }
        );
    }
}

