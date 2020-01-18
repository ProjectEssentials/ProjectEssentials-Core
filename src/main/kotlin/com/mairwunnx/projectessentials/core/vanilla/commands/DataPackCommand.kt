/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

@file:Suppress("DuplicatedCode")

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.google.common.collect.Lists
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.resources.ResourcePackInfo
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.dimension.DimensionType
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer
import java.util.function.Function

internal object DataPackCommand {
    private val UNKNOWN_DATA_PACK_EXCEPTION =
        DynamicCommandExceptionType(
            Function { p_208808_0_: Any? ->
                TranslationTextComponent(
                    "commands.datapack.unknown", p_208808_0_
                )
            }
        )
    private val ENABLE_FAILED_EXCEPTION = DynamicCommandExceptionType(
        Function { p_208818_0_: Any? ->
            TranslationTextComponent(
                "commands.datapack.enable.failed", p_208818_0_
            )
        }
    )
    private val DISABLE_FAILED_EXCEPTION = DynamicCommandExceptionType(
        Function { p_208815_0_: Any? ->
            TranslationTextComponent(
                "commands.datapack.disable.failed", p_208815_0_
            )
        }
    )
    private val SUGGEST_ENABLED_PACK =
        SuggestionProvider { p_198305_0_: CommandContext<CommandSource>, p_198305_1_ ->
            ISuggestionProvider.suggest(
                p_198305_0_.source.server.resourcePacks.enabledPacks.stream().map { obj ->
                    obj.name
                }.map { input ->
                    StringArgumentType.escapeIfRequired(input)
                }, p_198305_1_
            )
        }
    private val SUGGEST_AVAILABLE_PACK =
        SuggestionProvider { p_198296_0_: CommandContext<CommandSource>, p_198296_1_ ->
            ISuggestionProvider.suggest(
                p_198296_0_.source.server.resourcePacks.availablePacks.stream().map { obj ->
                    obj.name
                }.map { input ->
                    StringArgumentType.escapeIfRequired(input)
                }, p_198296_1_
            )
        }

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.datapack + "datapack"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["datapack"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/datapack` vanilla command")
        NativeCommandUtils.removeCommand("datapack")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("enable").then(
                        Commands.argument(
                            "name", StringArgumentType.string()
                        ).suggests(SUGGEST_AVAILABLE_PACK).executes { p_198292_0_ ->
                            enablePack(p_198292_0_.source,
                                parsePackInfo(p_198292_0_, "name", true),
                                object : IHandler {
                                    override fun apply(
                                        p_apply_1_: MutableList<ResourcePackInfo>,
                                        p_apply_2_: ResourcePackInfo
                                    ) {
                                        p_apply_2_.priority.func_198993_a(
                                            p_apply_1_,
                                            p_apply_2_,
                                            { p_198304_0_ -> p_198304_0_ },
                                            false
                                        )
                                    }
                                }
                            )
                        }.then(
                            Commands.literal("after").then(
                                Commands.argument(
                                    "existing",
                                    StringArgumentType.string()
                                ).suggests(SUGGEST_ENABLED_PACK).executes { p_198307_0_ ->
                                    enablePack(p_198307_0_.source,
                                        parsePackInfo(p_198307_0_, "name", true),
                                        object : IHandler {
                                            override fun apply(
                                                p_apply_1_: MutableList<ResourcePackInfo>,
                                                p_apply_2_: ResourcePackInfo
                                            ) {
                                                p_apply_1_.add(
                                                    p_apply_1_.indexOf(
                                                        parsePackInfo(
                                                            p_198307_0_,
                                                            "existing",
                                                            false
                                                        )
                                                    ) + 1, p_apply_2_
                                                )
                                            }
                                        }
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("before").then(
                                Commands.argument(
                                    "existing",
                                    StringArgumentType.string()
                                ).suggests(SUGGEST_ENABLED_PACK).executes { p_198311_0_ ->
                                    enablePack(p_198311_0_.source,
                                        parsePackInfo(p_198311_0_, "name", true),
                                        object : IHandler {
                                            override fun apply(
                                                p_apply_1_: MutableList<ResourcePackInfo>,
                                                p_apply_2_: ResourcePackInfo
                                            ) {
                                                p_apply_1_.add(
                                                    p_apply_1_.indexOf(
                                                        parsePackInfo(
                                                            p_198311_0_,
                                                            "existing",
                                                            false
                                                        )
                                                    ), p_apply_2_
                                                )
                                            }
                                        }
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("last").executes { p_198298_0_ ->
                                enablePack(p_198298_0_.source,
                                    parsePackInfo(p_198298_0_, "name", true),
                                    object : IHandler {
                                        override fun apply(
                                            p_apply_1_: MutableList<ResourcePackInfo>,
                                            p_apply_2_: ResourcePackInfo
                                        ) {
                                            p_apply_1_.add(p_apply_2_)
                                        }
                                    }
                                )
                            }
                        ).then(
                            Commands.literal("first").executes { p_198300_0_ ->
                                enablePack(p_198300_0_.source,
                                    parsePackInfo(p_198300_0_, "name", true),
                                    object : IHandler {
                                        override fun apply(
                                            p_apply_1_: MutableList<ResourcePackInfo>,
                                            p_apply_2_: ResourcePackInfo
                                        ) {
                                            p_apply_1_.add(0, p_apply_2_)
                                        }
                                    }
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("disable").then(
                        Commands.argument(
                            "name", StringArgumentType.string()
                        ).suggests(SUGGEST_ENABLED_PACK).executes { p_198295_0_ ->
                            disablePack(
                                p_198295_0_.source,
                                parsePackInfo(p_198295_0_, "name", false)
                            )
                        }
                    )
                ).then(
                    Commands.literal("list").executes { p_198290_0_ ->
                        listAllPacks(p_198290_0_.source)
                    }.then(
                        Commands.literal("available").executes { p_198288_0_ ->
                            listAvailablePacks(p_198288_0_.source)
                        }
                    ).then(
                        Commands.literal("enabled").executes { p_198309_0_ ->
                            listEnabledPacks(p_198309_0_.source)
                        }
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.datapack", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "datapack")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.datapack.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    /**
     * Enables the given pack.
     *
     * @return The number of packs that are loaded after this operation.
     */
    @Throws(CommandSyntaxException::class)
    private fun enablePack(
        source: CommandSource,
        pack: ResourcePackInfo,
        priorityCallback: IHandler
    ): Int {
        checkPermissions(source)

        val resourcepacklist = source.server.resourcePacks
        val list: MutableList<ResourcePackInfo> = Lists.newArrayList(resourcepacklist.enabledPacks)
        priorityCallback.apply(list, pack)
        resourcepacklist.enabledPacks = list
        val worldinfo = source.server.getWorld(DimensionType.OVERWORLD).worldInfo
        worldinfo.enabledDataPacks.clear()
        resourcepacklist.enabledPacks.forEach(Consumer { p_198294_1_ ->
            worldinfo.enabledDataPacks.add(p_198294_1_.name)
        })
        worldinfo.disabledDataPacks.remove(pack.name)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.datapack.enable.success",
                pack.func_195794_a(true)
            ), true
        )
        source.server.reload()
        return resourcepacklist.enabledPacks.size
    }

    /**
     * Disables the given pack.
     *
     * @return The number of packs that are loaded after this operation.
     */
    private fun disablePack(source: CommandSource, pack: ResourcePackInfo): Int {
        checkPermissions(source)

        val resourcepacklist = source.server.resourcePacks
        val list: MutableList<ResourcePackInfo> = Lists.newArrayList(resourcepacklist.enabledPacks)
        list.remove(pack)
        resourcepacklist.enabledPacks = list
        val worldinfo = source.server.getWorld(DimensionType.OVERWORLD).worldInfo
        worldinfo.enabledDataPacks.clear()
        resourcepacklist.enabledPacks.forEach(Consumer { p_198291_1_ ->
            worldinfo.enabledDataPacks.add(p_198291_1_.name)
        })
        worldinfo.disabledDataPacks.add(pack.name)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.datapack.disable.success",
                pack.func_195794_a(true)
            ), true
        )
        source.server.reload()
        return resourcepacklist.enabledPacks.size
    }

    /**
     * Sends a list of both enabled and available packs to the user.
     *
     * @return The total number of packs.
     */
    private fun listAllPacks(source: CommandSource): Int {
        checkPermissions(source)
        return listEnabledPacks(source) + listAvailablePacks(source)
    }

    /**
     * Sends a list of available packs to the user.
     *
     * @return The number of available packs.
     */
    private fun listAvailablePacks(source: CommandSource): Int {
        checkPermissions(source)

        val resourcepacklist = source.server.resourcePacks
        if (resourcepacklist.availablePacks.isEmpty()) {
            source.sendFeedback(
                TranslationTextComponent("commands.datapack.list.available.none"),
                false
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.datapack.list.available.success",
                    resourcepacklist.availablePacks.size,
                    TextComponentUtils.makeList(
                        resourcepacklist.availablePacks
                    ) { p_198293_0_ ->
                        p_198293_0_.func_195794_a(false)
                    }
                ), false
            )
        }
        return resourcepacklist.availablePacks.size
    }

    /**
     * Sends a list of enabled packs to the user.
     *
     * @return The number of enabled packs.
     */
    private fun listEnabledPacks(source: CommandSource): Int {
        checkPermissions(source)

        val resourcepacklist = source.server.resourcePacks
        if (resourcepacklist.enabledPacks.isEmpty()) {
            source.sendFeedback(
                TranslationTextComponent("commands.datapack.list.enabled.none"),
                false
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.datapack.list.enabled.success",
                    resourcepacklist.enabledPacks.size,
                    TextComponentUtils.makeList(
                        resourcepacklist.enabledPacks
                    ) { p_198306_0_ ->
                        p_198306_0_.func_195794_a(true)
                    }
                ), false
            )
        }
        return resourcepacklist.enabledPacks.size
    }

    @Throws(CommandSyntaxException::class)
    private fun parsePackInfo(
        context: CommandContext<CommandSource>,
        name: String,
        enabling: Boolean
    ): ResourcePackInfo {
        val s = StringArgumentType.getString(context, name)
        val resourcepacklist = context.source.server.resourcePacks
        val resourcepackinfo = resourcepacklist.getPackInfo(s)
        return if (resourcepackinfo == null) {
            throw UNKNOWN_DATA_PACK_EXCEPTION.create(s)
        } else {
            val flag = resourcepacklist.enabledPacks.contains(resourcepackinfo)
            if (enabling && flag) {
                throw ENABLE_FAILED_EXCEPTION.create(s)
            } else if (!enabling && !flag) {
                throw DISABLE_FAILED_EXCEPTION.create(s)
            } else {
                resourcepackinfo
            }
        }
    }

    internal interface IHandler {
        @Throws(CommandSyntaxException::class)
        fun apply(
            p_apply_1_: MutableList<ResourcePackInfo>,
            p_apply_2_: ResourcePackInfo
        )
    }
}
