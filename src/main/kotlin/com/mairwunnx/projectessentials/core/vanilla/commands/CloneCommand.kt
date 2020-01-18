/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.google.common.collect.Lists
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.command.arguments.BlockPredicateArgument
import net.minecraft.inventory.IClearable
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.CachedBlockInfo
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MutableBoundingBox
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.function.Predicate

@Suppress("DEPRECATION")
internal object CloneCommand {
    private val OVERLAP_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.clone.overlap")
    )
    private val CLONE_TOO_BIG_EXCEPTION =
        Dynamic2CommandExceptionType(
            Dynamic2CommandExceptionType.Function { p_208796_0_: Any?, p_208796_1_: Any? ->
                TranslationTextComponent(
                    "commands.clone.toobig",
                    p_208796_0_,
                    p_208796_1_
                )
            }
        )
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.clone.failed")
    )

    @Suppress("MemberVisibilityCanBePrivate")
    val NOT_AIR =
        Predicate { p_198275_0_: CachedBlockInfo -> !p_198275_0_.blockState.isAir }

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.clone + "clone"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["clone"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/clone` vanilla command")
        NativeCommandUtils.removeCommand("clone")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument("begin", BlockPosArgument.blockPos()).then(
                        Commands.argument("end", BlockPosArgument.blockPos()).then(
                            Commands.argument(
                                "destination", BlockPosArgument.blockPos()
                            ).executes { p_198264_0_ ->
                                doClone(
                                    p_198264_0_.source,
                                    BlockPosArgument.getLoadedBlockPos(p_198264_0_, "begin"),
                                    BlockPosArgument.getLoadedBlockPos(p_198264_0_, "end"),
                                    BlockPosArgument.getLoadedBlockPos(p_198264_0_, "destination"),
                                    Predicate { true },
                                    Mode.NORMAL
                                )
                            }.then(
                                Commands.literal("replace").executes { p_198268_0_ ->
                                    doClone(
                                        p_198268_0_.source,
                                        BlockPosArgument.getLoadedBlockPos(p_198268_0_, "begin"),
                                        BlockPosArgument.getLoadedBlockPos(p_198268_0_, "end"),
                                        BlockPosArgument.getLoadedBlockPos(
                                            p_198268_0_, "destination"
                                        ),
                                        Predicate { true },
                                        Mode.NORMAL
                                    )
                                }.then(
                                    Commands.literal("force").executes { p_198277_0_ ->
                                        doClone(
                                            p_198277_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198277_0_, "begin"
                                            ),
                                            BlockPosArgument.getLoadedBlockPos(p_198277_0_, "end"),
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198277_0_, "destination"
                                            ),
                                            Predicate { true },
                                            Mode.FORCE
                                        )
                                    }
                                ).then(
                                    Commands.literal("move").executes { p_198280_0_ ->
                                        doClone(
                                            p_198280_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198280_0_, "begin"
                                            ),
                                            BlockPosArgument.getLoadedBlockPos(p_198280_0_, "end"),
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198280_0_, "destination"
                                            ),
                                            Predicate { true },
                                            Mode.MOVE
                                        )
                                    }
                                ).then(
                                    Commands.literal("normal").executes { p_198270_0_ ->
                                        doClone(
                                            p_198270_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198270_0_, "begin"
                                            ),
                                            BlockPosArgument.getLoadedBlockPos(p_198270_0_, "end"),
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198270_0_, "destination"
                                            ),
                                            Predicate { true },
                                            Mode.NORMAL
                                        )
                                    }
                                )
                            ).then(
                                Commands.literal("masked").executes { p_198276_0_ ->
                                    doClone(
                                        p_198276_0_.source,
                                        BlockPosArgument.getLoadedBlockPos(p_198276_0_, "begin"),
                                        BlockPosArgument.getLoadedBlockPos(p_198276_0_, "end"),
                                        BlockPosArgument.getLoadedBlockPos(
                                            p_198276_0_, "destination"
                                        ),
                                        NOT_AIR,
                                        Mode.NORMAL
                                    )
                                }.then(
                                    Commands.literal("force").executes { p_198282_0_ ->
                                        doClone(
                                            p_198282_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198282_0_, "begin"
                                            ),
                                            BlockPosArgument.getLoadedBlockPos(p_198282_0_, "end"),
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198282_0_, "destination"
                                            ),
                                            NOT_AIR,
                                            Mode.FORCE
                                        )
                                    }
                                ).then(
                                    Commands.literal("move").executes { p_198263_0_ ->
                                        doClone(
                                            p_198263_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198263_0_, "begin"
                                            ),
                                            BlockPosArgument.getLoadedBlockPos(p_198263_0_, "end"),
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198263_0_, "destination"
                                            ),
                                            NOT_AIR,
                                            Mode.MOVE
                                        )
                                    }
                                ).then(
                                    Commands.literal("normal").executes { p_198266_0_ ->
                                        doClone(
                                            p_198266_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198266_0_, "begin"
                                            ),
                                            BlockPosArgument.getLoadedBlockPos(p_198266_0_, "end"),
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198266_0_, "destination"
                                            ),
                                            NOT_AIR,
                                            Mode.NORMAL
                                        )
                                    }
                                )
                            ).then(
                                Commands.literal("filtered").then(
                                    Commands.argument(
                                        "filter",
                                        BlockPredicateArgument.blockPredicate()
                                    ).executes { p_198273_0_ ->
                                        doClone(
                                            p_198273_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198273_0_, "begin"
                                            ),
                                            BlockPosArgument.getLoadedBlockPos(p_198273_0_, "end"),
                                            BlockPosArgument.getLoadedBlockPos(
                                                p_198273_0_, "destination"
                                            ),
                                            BlockPredicateArgument.getBlockPredicate(
                                                p_198273_0_, "filter"
                                            ),
                                            Mode.NORMAL
                                        )
                                    }.then(
                                        Commands.literal("force").executes { p_198267_0_ ->
                                            doClone(
                                                p_198267_0_.source,
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198267_0_, "begin"
                                                ),
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198267_0_, "end"
                                                ),
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198267_0_, "destination"
                                                ),
                                                BlockPredicateArgument.getBlockPredicate(
                                                    p_198267_0_, "filter"
                                                ),
                                                Mode.FORCE
                                            )
                                        }
                                    ).then(
                                        Commands.literal("move").executes { p_198261_0_ ->
                                            doClone(
                                                p_198261_0_.source,
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198261_0_, "begin"
                                                ),
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198261_0_, "end"
                                                ),
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198261_0_, "destination"
                                                ),
                                                BlockPredicateArgument.getBlockPredicate(
                                                    p_198261_0_, "filter"
                                                ),
                                                Mode.MOVE
                                            )
                                        }
                                    ).then(
                                        Commands.literal("normal").executes { p_198278_0_ ->
                                            doClone(
                                                p_198278_0_.source,
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198278_0_, "begin"
                                                ),
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198278_0_, "end"
                                                ),
                                                BlockPosArgument.getLoadedBlockPos(
                                                    p_198278_0_, "destination"
                                                ),
                                                BlockPredicateArgument.getBlockPredicate(
                                                    p_198278_0_, "filter"
                                                ),
                                                Mode.NORMAL
                                            )
                                        }
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.clone", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "clone")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.clone.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun doClone(
        source: CommandSource,
        beginPos: BlockPos,
        endPos: BlockPos,
        destPos: BlockPos,
        filterPredicate: Predicate<CachedBlockInfo>,
        cloneMode: Mode
    ): Int {
        checkPermissions(source)

        val mutableboundingbox = MutableBoundingBox(beginPos, endPos)
        val blockpos = destPos.add(mutableboundingbox.length)
        val mutableboundingbox1 = MutableBoundingBox(destPos, blockpos)
        return if (!cloneMode.allowsOverlap() && mutableboundingbox1.intersectsWith(
                mutableboundingbox
            )
        ) {
            throw OVERLAP_EXCEPTION.create()
        } else {
            val i =
                mutableboundingbox.xSize * mutableboundingbox.ySize * mutableboundingbox.zSize
            if (i > 32768) {
                throw CLONE_TOO_BIG_EXCEPTION.create(32768, i)
            } else {
                val serverworld = source.world
                if (serverworld.isAreaLoaded(beginPos, endPos) && serverworld.isAreaLoaded(
                        destPos, blockpos
                    )
                ) {
                    val list: MutableList<BlockInfo> = Lists.newArrayList()
                    val list1: MutableList<BlockInfo> = Lists.newArrayList()
                    val list2: MutableList<BlockInfo> = Lists.newArrayList()
                    val deque: Deque<BlockPos> = Lists.newLinkedList()
                    val blockpos1 = BlockPos(
                        mutableboundingbox1.minX - mutableboundingbox.minX,
                        mutableboundingbox1.minY - mutableboundingbox.minY,
                        mutableboundingbox1.minZ - mutableboundingbox.minZ
                    )
                    for (j in mutableboundingbox.minZ..mutableboundingbox.maxZ) {
                        for (k in mutableboundingbox.minY..mutableboundingbox.maxY) {
                            for (l in mutableboundingbox.minX..mutableboundingbox.maxX) {
                                val blockpos2 = BlockPos(l, k, j)
                                val blockpos3 = blockpos2.add(blockpos1)
                                val cachedblockinfo = CachedBlockInfo(
                                    serverworld, blockpos2, false
                                )
                                val blockstate = cachedblockinfo.blockState
                                if (filterPredicate.test(cachedblockinfo)) {
                                    val tileentity = serverworld.getTileEntity(blockpos2)
                                    if (tileentity != null) {
                                        val compoundnbt = tileentity.write(
                                            CompoundNBT()
                                        )
                                        list1.add(
                                            BlockInfo(blockpos3, blockstate, compoundnbt)
                                        )
                                        deque.addLast(blockpos2)
                                    } else if (!blockstate.isOpaqueCube(
                                            serverworld,
                                            blockpos2
                                        ) && !blockstate.func_224756_o(serverworld, blockpos2)
                                    ) {
                                        list2.add(
                                            BlockInfo(blockpos3, blockstate, null as CompoundNBT?)
                                        )
                                        deque.addFirst(blockpos2)
                                    } else {
                                        list.add(
                                            BlockInfo(blockpos3, blockstate, null as CompoundNBT?)
                                        )
                                        deque.addLast(blockpos2)
                                    }
                                }
                            }
                        }
                    }
                    if (cloneMode == Mode.MOVE) {
                        for (blockpos4 in deque) {
                            val tileentity1 = serverworld.getTileEntity(blockpos4)
                            IClearable.clearObj(tileentity1)
                            serverworld.setBlockState(blockpos4, Blocks.BARRIER.defaultState, 2)
                        }
                        for (blockpos5 in deque) {
                            serverworld.setBlockState(blockpos5, Blocks.AIR.defaultState, 3)
                        }
                    }
                    val list3: MutableList<BlockInfo> = Lists.newArrayList()
                    list3.addAll(list)
                    list3.addAll(list1)
                    list3.addAll(list2)
                    val list4 = Lists.reverse(list3)
                    for (`clonecommand$blockinfo` in list4) {
                        val tileentity2 = serverworld.getTileEntity(
                            `clonecommand$blockinfo`.pos
                        )
                        IClearable.clearObj(tileentity2)
                        serverworld.setBlockState(
                            `clonecommand$blockinfo`.pos,
                            Blocks.BARRIER.defaultState,
                            2
                        )
                    }
                    var i1 = 0
                    for (`clonecommand$blockinfo1` in list3) {
                        if (serverworld.setBlockState(
                                `clonecommand$blockinfo1`.pos,
                                `clonecommand$blockinfo1`.state,
                                2
                            )
                        ) ++i1
                    }
                    for (`clonecommand$blockinfo2` in list1) {
                        val tileentity3 =
                            serverworld.getTileEntity(`clonecommand$blockinfo2`.pos)
                        if (`clonecommand$blockinfo2`.tag != null && tileentity3 != null) {
                            `clonecommand$blockinfo2`.tag.putInt(
                                "x", `clonecommand$blockinfo2`.pos.x
                            )
                            `clonecommand$blockinfo2`.tag.putInt(
                                "y", `clonecommand$blockinfo2`.pos.y
                            )
                            `clonecommand$blockinfo2`.tag.putInt(
                                "z", `clonecommand$blockinfo2`.pos.z
                            )
                            tileentity3.read(`clonecommand$blockinfo2`.tag)
                            tileentity3.markDirty()
                        }
                        serverworld.setBlockState(
                            `clonecommand$blockinfo2`.pos,
                            `clonecommand$blockinfo2`.state,
                            2
                        )
                    }
                    for (`clonecommand$blockinfo3` in list4) {
                        serverworld.notifyNeighbors(
                            `clonecommand$blockinfo3`.pos,
                            `clonecommand$blockinfo3`.state.block
                        )
                    }
                    serverworld.pendingBlockTicks.copyTicks(mutableboundingbox, blockpos1)
                    if (i1 == 0) {
                        throw FAILED_EXCEPTION.create()
                    } else {
                        source.sendFeedback(
                            TranslationTextComponent("commands.clone.success", i1),
                            true
                        )
                        i1
                    }
                } else {
                    throw BlockPosArgument.POS_UNLOADED.create()
                }
            }
        }
    }

    internal class BlockInfo(
        val pos: BlockPos,
        val state: BlockState,
        val tag: CompoundNBT?
    )

    internal enum class Mode(private val allowOverlap: Boolean) {
        FORCE(true), MOVE(true), NORMAL(false);

        fun allowsOverlap(): Boolean = allowOverlap
    }
}
