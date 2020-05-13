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
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.ComponentArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ResourceLocationArgument
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.CustomServerBossInfo
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.BossInfo
import java.util.function.Function

internal object BossBarCommand : VanillaCommandBase() {
    private val BOSS_BAR_ID_TAKEN = DynamicCommandExceptionType(
        Function { p_208783_0_: Any? ->
            TranslationTextComponent(
                "commands.bossbar.create.failed",
                p_208783_0_
            )
        }
    )
    private val NO_BOSSBAR_WITH_ID = DynamicCommandExceptionType(
        Function { p_208782_0_: Any? ->
            TranslationTextComponent(
                "commands.bossbar.unknown",
                p_208782_0_
            )
        }
    )
    private val PLAYERS_ALREADY_ON_BOSSBAR = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.players.unchanged")
    )
    private val ALREADY_NAME_OF_BOSSBAR = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.name.unchanged")
    )
    private val ALREADY_COLOR_OF_BOSSBAR = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.color.unchanged")
    )
    private val ALREADY_STYLE_OF_BOSSBAR = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.style.unchanged")
    )
    private val ALREADY_VALUE_OF_BOSSBAR = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.value.unchanged")
    )
    private val ALREADY_MAX_OF_BOSSBAR = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.max.unchanged")
    )
    private val BOSSBAR_ALREADY_HIDDEN = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.visibility.unchanged.hidden")
    )
    private val BOSSBAR_ALREADY_VISIBLE = SimpleCommandExceptionType(
        TranslationTextComponent("commands.bossbar.set.visibility.unchanged.visible")
    )
    val SUGGESTIONS_PROVIDER =
        SuggestionProvider { p_201404_0_: CommandContext<CommandSource>, p_201404_1_ ->
            ISuggestionProvider.suggestIterable(
                p_201404_0_.source.server.customBossEvents.iDs,
                p_201404_1_
            )
        }

    private var aliases = configuration.take().aliases.bossbar + "bossbar"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["bossbar"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("bossbar")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "id", ResourceLocationArgument.resourceLocation()
                        ).then(
                            Commands.argument(
                                "name", ComponentArgument.component()
                            ).executes { p_201426_0_ ->
                                createBossbar(
                                    p_201426_0_.source,
                                    ResourceLocationArgument.getResourceLocation(p_201426_0_, "id"),
                                    ComponentArgument.getComponent(p_201426_0_, "name")
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument(
                            "id", ResourceLocationArgument.resourceLocation()
                        ).suggests(SUGGESTIONS_PROVIDER).executes { p_201429_0_ ->
                            removeBossbar(p_201429_0_.source, getBossbar(p_201429_0_))
                        }
                    )
                ).then(
                    Commands.literal("list").executes { p_201396_0_ ->
                        listBars(p_201396_0_.source)
                    }
                ).then(
                    Commands.literal("set").then(
                        Commands.argument(
                            "id", ResourceLocationArgument.resourceLocation()
                        ).suggests(SUGGESTIONS_PROVIDER).then(
                            Commands.literal("name").then(
                                Commands.argument(
                                    "name", ComponentArgument.component()
                                ).executes { p_201401_0_ ->
                                    setName(
                                        p_201401_0_.source,
                                        getBossbar(p_201401_0_),
                                        ComponentArgument.getComponent(p_201401_0_, "name")
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("color").then(
                                Commands.literal("pink").executes { p_201409_0_ ->
                                    setColor(
                                        p_201409_0_.source,
                                        getBossbar(p_201409_0_),
                                        BossInfo.Color.PINK
                                    )
                                }
                            ).then(
                                Commands.literal("blue").executes { p_201422_0_ ->
                                    setColor(
                                        p_201422_0_.source,
                                        getBossbar(p_201422_0_),
                                        BossInfo.Color.BLUE
                                    )
                                }
                            ).then(
                                Commands.literal("red").executes { p_201417_0_ ->
                                    setColor(
                                        p_201417_0_.source,
                                        getBossbar(p_201417_0_),
                                        BossInfo.Color.RED
                                    )
                                }
                            ).then(
                                Commands.literal("green").executes { p_201424_0_ ->
                                    setColor(
                                        p_201424_0_.source,
                                        getBossbar(p_201424_0_),
                                        BossInfo.Color.GREEN
                                    )
                                }
                            ).then(
                                Commands.literal("yellow").executes { p_201393_0_ ->
                                    setColor(
                                        p_201393_0_.source,
                                        getBossbar(p_201393_0_),
                                        BossInfo.Color.YELLOW
                                    )
                                }
                            ).then(
                                Commands.literal("purple").executes { p_201391_0_ ->
                                    setColor(
                                        p_201391_0_.source,
                                        getBossbar(p_201391_0_),
                                        BossInfo.Color.PURPLE
                                    )
                                }
                            ).then(
                                Commands.literal("white").executes { p_201406_0_ ->
                                    setColor(
                                        p_201406_0_.source,
                                        getBossbar(p_201406_0_),
                                        BossInfo.Color.WHITE
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("style").then(
                                Commands.literal("progress").executes { p_201399_0_ ->
                                    setStyle(
                                        p_201399_0_.source,
                                        getBossbar(p_201399_0_),
                                        BossInfo.Overlay.PROGRESS
                                    )
                                }
                            ).then(
                                Commands.literal("notched_6").executes { p_201419_0_ ->
                                    setStyle(
                                        p_201419_0_.source,
                                        getBossbar(p_201419_0_),
                                        BossInfo.Overlay.NOTCHED_6
                                    )
                                }
                            ).then(
                                Commands.literal("notched_10").executes { p_201412_0_ ->
                                    setStyle(
                                        p_201412_0_.source,
                                        getBossbar(p_201412_0_),
                                        BossInfo.Overlay.NOTCHED_10
                                    )
                                }
                            ).then(
                                Commands.literal("notched_12").executes { p_201421_0_ ->
                                    setStyle(
                                        p_201421_0_.source,
                                        getBossbar(p_201421_0_),
                                        BossInfo.Overlay.NOTCHED_12
                                    )
                                }
                            ).then(
                                Commands.literal("notched_20").executes { p_201403_0_ ->
                                    setStyle(
                                        p_201403_0_.source,
                                        getBossbar(p_201403_0_),
                                        BossInfo.Overlay.NOTCHED_20
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("value").then(
                                Commands.argument(
                                    "value", IntegerArgumentType.integer(0)
                                ).executes { p_201408_0_ ->
                                    setValue(
                                        p_201408_0_.source,
                                        getBossbar(p_201408_0_),
                                        IntegerArgumentType.getInteger(p_201408_0_, "value")
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("max").then(
                                Commands.argument(
                                    "max", IntegerArgumentType.integer(1)
                                ).executes { p_201395_0_ ->
                                    setMax(
                                        p_201395_0_.source,
                                        getBossbar(p_201395_0_),
                                        IntegerArgumentType.getInteger(p_201395_0_, "max")
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("visible").then(
                                Commands.argument(
                                    "visible", BoolArgumentType.bool()
                                ).executes { p_201427_0_ ->
                                    setVisibility(
                                        p_201427_0_.source,
                                        getBossbar(p_201427_0_),
                                        BoolArgumentType.getBool(p_201427_0_, "visible")
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("players").executes { p_201430_0_ ->
                                setPlayers(
                                    p_201430_0_.source, getBossbar(p_201430_0_), emptyList()
                                )
                            }.then(
                                Commands.argument(
                                    "targets", EntityArgument.players()
                                ).executes { p_201411_0_ ->
                                    setPlayers(
                                        p_201411_0_.source,
                                        getBossbar(p_201411_0_),
                                        EntityArgument.getPlayersAllowingNone(
                                            p_201411_0_, "targets"
                                        )
                                    )
                                }
                            )
                        )
                    )
                ).then(
                    Commands.literal("get").then(
                        Commands.argument(
                            "id", ResourceLocationArgument.resourceLocation()
                        ).suggests(SUGGESTIONS_PROVIDER).then(
                            Commands.literal("value").executes { p_201418_0_ ->
                                getValue(p_201418_0_.source, getBossbar(p_201418_0_))
                            }
                        ).then(
                            Commands.literal("max").executes { p_201398_0_ ->
                                getMax(p_201398_0_.source, getBossbar(p_201398_0_))
                            }
                        ).then(
                            Commands.literal("visible").executes { p_201392_0_ ->
                                getVisibility(p_201392_0_.source, getBossbar(p_201392_0_))
                            }
                        ).then(
                            Commands.literal("players").executes { p_201388_0_ ->
                                getPlayers(p_201388_0_.source, getBossbar(p_201388_0_))
                            }
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.bossbar", 2)) {
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
                                "native.bossbar", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }


    private fun getValue(source: CommandSource, bossbar: CustomServerBossInfo): Int {
        checkPermissions(source)

        source.sendFeedback(
            TranslationTextComponent(
                "commands.bossbar.get.value",
                bossbar.formattedName, bossbar.value
            ), true
        )
        return bossbar.value
    }

    private fun getMax(source: CommandSource, bossbar: CustomServerBossInfo): Int {
        checkPermissions(source)

        source.sendFeedback(
            TranslationTextComponent(
                "commands.bossbar.get.max",
                bossbar.formattedName, bossbar.max
            ), true
        )
        return bossbar.max
    }

    private fun getVisibility(source: CommandSource, bossbar: CustomServerBossInfo): Int {
        checkPermissions(source)

        return if (bossbar.isVisible) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.get.visible.visible",
                    bossbar.formattedName
                ), true
            )
            1
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.get.visible.hidden",
                    bossbar.formattedName
                ), true
            )
            0
        }
    }

    private fun getPlayers(source: CommandSource, bossbar: CustomServerBossInfo): Int {
        checkPermissions(source)

        if (bossbar.players.isEmpty()) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.get.players.none",
                    bossbar.formattedName
                ), true
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.get.players.some",
                    bossbar.formattedName,
                    bossbar.players.size,
                    TextComponentUtils.makeList(
                        bossbar.players
                    ) { obj: ServerPlayerEntity -> obj.displayName }
                ), true
            )
        }
        return bossbar.players.size
    }

    @Throws(CommandSyntaxException::class)
    private fun setVisibility(
        source: CommandSource,
        bossbar: CustomServerBossInfo,
        visible: Boolean
    ): Int {
        checkPermissions(source)

        return if (bossbar.isVisible == visible) {
            if (visible) {
                throw BOSSBAR_ALREADY_VISIBLE.create()
            } else {
                throw BOSSBAR_ALREADY_HIDDEN.create()
            }
        } else {
            bossbar.isVisible = visible
            if (visible) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.bossbar.set.visible.success.visible",
                        bossbar.formattedName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.bossbar.set.visible.success.hidden",
                        bossbar.formattedName
                    ), true
                )
            }
            0
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setValue(source: CommandSource, bossbar: CustomServerBossInfo, value: Int): Int {
        checkPermissions(source)

        return if (bossbar.value == value) {
            throw ALREADY_VALUE_OF_BOSSBAR.create()
        } else {
            bossbar.value = value
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.set.value.success",
                    bossbar.formattedName,
                    value
                ), true
            )
            value
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setMax(source: CommandSource, bossbar: CustomServerBossInfo, max: Int): Int {
        checkPermissions(source)

        return if (bossbar.max == max) {
            throw ALREADY_MAX_OF_BOSSBAR.create()
        } else {
            bossbar.max = max
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.set.max.success",
                    bossbar.formattedName, max
                ), true
            )
            max
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setColor(
        source: CommandSource,
        bossbar: CustomServerBossInfo,
        color: BossInfo.Color
    ): Int {
        checkPermissions(source)

        return if (bossbar.color == color) {
            throw ALREADY_COLOR_OF_BOSSBAR.create()
        } else {
            bossbar.color = color
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.set.color.success",
                    bossbar.formattedName
                ), true
            )
            0
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setStyle(
        source: CommandSource,
        bossbar: CustomServerBossInfo,
        styleIn: BossInfo.Overlay
    ): Int {
        checkPermissions(source)

        return if (bossbar.overlay == styleIn) {
            throw ALREADY_STYLE_OF_BOSSBAR.create()
        } else {
            bossbar.overlay = styleIn
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.set.style.success",
                    bossbar.formattedName
                ), true
            )
            0
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setName(
        source: CommandSource,
        bossbar: CustomServerBossInfo,
        name: ITextComponent
    ): Int {
        checkPermissions(source)

        val itextcomponent = TextComponentUtils.updateForEntity(
            source, name, null as Entity?, 0
        )
        return if (bossbar.name == itextcomponent) {
            throw ALREADY_NAME_OF_BOSSBAR.create()
        } else {
            bossbar.name = itextcomponent
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.set.name.success",
                    bossbar.formattedName
                ), true
            )
            0
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun setPlayers(
        source: CommandSource,
        bossbar: CustomServerBossInfo,
        players: Collection<ServerPlayerEntity>
    ): Int {
        checkPermissions(source)

        val flag = bossbar.setPlayers(players)
        return if (!flag) {
            throw PLAYERS_ALREADY_ON_BOSSBAR.create()
        } else {
            if (bossbar.players.isEmpty()) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.bossbar.set.players.success.none",
                        bossbar.formattedName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.bossbar.set.players.success.some",
                        bossbar.formattedName,
                        players.size,
                        TextComponentUtils.makeList(
                            players
                        ) { obj: ServerPlayerEntity -> obj.displayName }
                    ), true
                )
            }
            bossbar.players.size
        }
    }

    private fun listBars(source: CommandSource): Int {
        checkPermissions(source)

        val collection =
            source.server.customBossEvents.bossbars
        if (collection.isEmpty()) {
            source.sendFeedback(TranslationTextComponent("commands.bossbar.list.bars.none"), false)
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.list.bars.some",
                    collection.size,
                    TextComponentUtils.makeList(
                        collection
                    ) { obj: CustomServerBossInfo -> obj.formattedName }
                ), false
            )
        }
        return collection.size
    }

    @Throws(CommandSyntaxException::class)
    private fun createBossbar(
        source: CommandSource,
        id: ResourceLocation,
        displayName: ITextComponent
    ): Int {
        checkPermissions(source)

        val customserverbossinfomanager =
            source.server.customBossEvents
        return if (customserverbossinfomanager[id] != null) {
            throw BOSS_BAR_ID_TAKEN.create(id.toString())
        } else {
            val customserverbossinfo = customserverbossinfomanager.add(
                id, TextComponentUtils.updateForEntity(
                    source, displayName, null as Entity?, 0
                )
            )
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.bossbar.create.success",
                    customserverbossinfo.formattedName
                ), true
            )
            customserverbossinfomanager.bossbars.size
        }
    }

    private fun removeBossbar(source: CommandSource, bossbar: CustomServerBossInfo): Int {
        checkPermissions(source)

        val customserverbossinfomanager = source.server.customBossEvents
        bossbar.removeAllPlayers()
        customserverbossinfomanager.remove(bossbar)
        source.sendFeedback(
            TranslationTextComponent(
                "commands.bossbar.remove.success",
                bossbar.formattedName
            ), true
        )
        return customserverbossinfomanager.bossbars.size
    }

    @Throws(CommandSyntaxException::class)
    fun getBossbar(source: CommandContext<CommandSource>): CustomServerBossInfo {
        val resourcelocation =
            ResourceLocationArgument.getResourceLocation(source, "id")
        val customserverbossinfo =
            source.source.server.customBossEvents[resourcelocation]
        return customserverbossinfo ?: throw NO_BOSSBAR_WITH_ID.create(resourcelocation.toString())
    }
}

