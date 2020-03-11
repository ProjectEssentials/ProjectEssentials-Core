/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.google.common.collect.Lists
import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.EntryPoint.Companion.hasPermission
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.advancements.Advancement
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ResourceLocationArgument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager


internal object AdvancementCommand {
    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.advancement + "advancement"

    private val SUGGEST_ADVANCEMENTS =
        SuggestionProvider { p_198206_0_: CommandContext<CommandSource>, p_198206_1_ ->
            val collection =
                p_198206_0_.source.server.advancementManager.allAdvancements
            ISuggestionProvider.func_212476_a(
                collection.stream().map { obj -> obj.id },
                p_198206_1_
            )
        }

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["advancement"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/advancement` vanilla command")
        NativeCommandUtils.removeCommand("advancement")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("grant").then(
                        Commands.argument(
                            "targets",
                            EntityArgument.players()
                        ).then(
                            Commands.literal("only").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198202_0_ ->
                                    forEachAdvancement(
                                        p_198202_0_.source,
                                        EntityArgument.getPlayers(p_198202_0_, "targets"),
                                        Action.GRANT,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198202_0_,
                                                "advancement"
                                            ), Mode.ONLY
                                        )
                                    )
                                }.then(
                                    Commands.argument(
                                        "criterion",
                                        StringArgumentType.greedyString()
                                    ).suggests { p_198209_0_, p_198209_1_ ->
                                        ISuggestionProvider.suggest(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198209_0_,
                                                "advancement"
                                            ).criteria.keys, p_198209_1_
                                        )
                                    }.executes { p_198212_0_ ->
                                        updateCriterion(
                                            p_198212_0_.source,
                                            EntityArgument.getPlayers(p_198212_0_, "targets"),
                                            Action.GRANT,
                                            ResourceLocationArgument.getAdvancement(
                                                p_198212_0_,
                                                "advancement"
                                            ),
                                            StringArgumentType.getString(p_198212_0_, "criterion")
                                        )
                                    }
                                )
                            )
                        ).then(
                            Commands.literal("from").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198215_0_ ->
                                    forEachAdvancement(
                                        p_198215_0_.source,
                                        EntityArgument.getPlayers(p_198215_0_, "targets"),
                                        Action.GRANT,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198215_0_,
                                                "advancement"
                                            ), Mode.FROM
                                        )
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("until").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198204_0_ ->
                                    forEachAdvancement(
                                        p_198204_0_.source,
                                        EntityArgument.getPlayers(p_198204_0_, "targets"),
                                        Action.GRANT,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198204_0_,
                                                "advancement"
                                            ), Mode.UNTIL
                                        )
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("through").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198211_0_ ->
                                    forEachAdvancement(
                                        p_198211_0_.source,
                                        EntityArgument.getPlayers(p_198211_0_, "targets"),
                                        Action.GRANT,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198211_0_,
                                                "advancement"
                                            ), Mode.THROUGH
                                        )
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("everything").executes { p_198217_0_ ->
                                forEachAdvancement(
                                    p_198217_0_.source,
                                    EntityArgument.getPlayers(p_198217_0_, "targets"),
                                    Action.GRANT,
                                    p_198217_0_.source.server.advancementManager.allAdvancements
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("revoke").then(
                        Commands.argument(
                            "targets",
                            EntityArgument.players()
                        ).then(
                            Commands.literal("only").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198198_0_ ->
                                    forEachAdvancement(
                                        p_198198_0_.source,
                                        EntityArgument.getPlayers(p_198198_0_, "targets"),
                                        Action.REVOKE,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198198_0_,
                                                "advancement"
                                            ), Mode.ONLY
                                        )
                                    )
                                }.then(
                                    Commands.argument(
                                        "criterion",
                                        StringArgumentType.greedyString()
                                    ).suggests { p_198210_0_, p_198210_1_ ->
                                        ISuggestionProvider.suggest(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198210_0_,
                                                "advancement"
                                            ).criteria.keys, p_198210_1_
                                        )
                                    }.executes { p_198200_0_ ->
                                        updateCriterion(
                                            p_198200_0_.source,
                                            EntityArgument.getPlayers(p_198200_0_, "targets"),
                                            Action.REVOKE,
                                            ResourceLocationArgument.getAdvancement(
                                                p_198200_0_,
                                                "advancement"
                                            ),
                                            StringArgumentType.getString(p_198200_0_, "criterion")
                                        )
                                    }
                                )
                            )
                        ).then(
                            Commands.literal("from").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198208_0_ ->
                                    forEachAdvancement(
                                        p_198208_0_.source,
                                        EntityArgument.getPlayers(p_198208_0_, "targets"),
                                        Action.REVOKE,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198208_0_,
                                                "advancement"
                                            ), Mode.FROM
                                        )
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("until").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198201_0_ ->
                                    forEachAdvancement(
                                        p_198201_0_.source,
                                        EntityArgument.getPlayers(p_198201_0_, "targets"),
                                        Action.REVOKE,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198201_0_,
                                                "advancement"
                                            ), Mode.UNTIL
                                        )
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("through").then(
                                Commands.argument(
                                    "advancement",
                                    ResourceLocationArgument.resourceLocation()
                                ).suggests(SUGGEST_ADVANCEMENTS).executes { p_198197_0_ ->
                                    forEachAdvancement(
                                        p_198197_0_.source,
                                        EntityArgument.getPlayers(p_198197_0_, "targets"),
                                        Action.REVOKE,
                                        getMatchingAdvancements(
                                            ResourceLocationArgument.getAdvancement(
                                                p_198197_0_,
                                                "advancement"
                                            ), Mode.THROUGH
                                        )
                                    )
                                }
                            )
                        ).then(
                            Commands.literal("everything").executes { p_198213_0_ ->
                                forEachAdvancement(
                                    p_198213_0_.source,
                                    EntityArgument.getPlayers(p_198213_0_, "targets"),
                                    Action.REVOKE,
                                    p_198213_0_.source.server.advancementManager.allAdvancements
                                )
                            }
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.advancement", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "advancement")
                )
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        LocalizationConfigurationUtils.getConfig().enabled,
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                LocalizationConfigurationUtils.getConfig().enabled,
                                "native.command.restricted_hover",
                                "native.advancement", "2"
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
     * Performs the given action on each advancement in the list, for each player.
     *
     * @return The number of affected advancements across all players.
     */
    private fun forEachAdvancement(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        action: Action,
        advancements: Collection<Advancement>
    ): Int {
        checkPermissions(source)

        var i = 0
        for (serverplayerentity in targets) {
            i += action.applyToAdvancements(serverplayerentity, advancements)
        }
        return if (i == 0) {
            if (advancements.size == 1) {
                if (targets.size == 1) {
                    throw CommandException(
                        TranslationTextComponent(
                            action.prefix + ".one.to.one.failure",
                            advancements.iterator().next().displayText,
                            targets.iterator().next().displayName
                        )
                    )
                } else {
                    throw CommandException(
                        TranslationTextComponent(
                            action.prefix + ".one.to.many.failure",
                            advancements.iterator().next().displayText,
                            targets.size
                        )
                    )
                }
            } else if (targets.size == 1) {
                throw CommandException(
                    TranslationTextComponent(
                        action.prefix + ".many.to.one.failure",
                        advancements.size,
                        targets.iterator().next().displayName
                    )
                )
            } else {
                throw CommandException(
                    TranslationTextComponent(
                        action.prefix + ".many.to.many.failure",
                        advancements.size,
                        targets.size
                    )
                )
            }
        } else {
            if (advancements.size == 1) {
                if (targets.size == 1) {
                    source.sendFeedback(
                        TranslationTextComponent(
                            action.prefix + ".one.to.one.success",
                            advancements.iterator().next().displayText,
                            targets.iterator().next().displayName
                        ), true
                    )
                } else {
                    source.sendFeedback(
                        TranslationTextComponent(
                            action.prefix + ".one.to.many.success",
                            advancements.iterator().next().displayText,
                            targets.size
                        ), true
                    )
                }
            } else if (targets.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        action.prefix + ".many.to.one.success",
                        advancements.size,
                        targets.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        action.prefix + ".many.to.many.success",
                        advancements.size,
                        targets.size
                    ), true
                )
            }
            i
        }
    }

    /**
     * Updates a single criterion based on the given action.
     *
     * @return The number of affected criteria across all players.
     */
    private fun updateCriterion(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        action: Action,
        advancementIn: Advancement,
        criterionName: String
    ): Int {
        checkPermissions(source)

        var i = 0
        return if (!advancementIn.criteria.containsKey(criterionName)) {
            throw CommandException(
                TranslationTextComponent(
                    "commands.advancement.criterionNotFound",
                    advancementIn.displayText,
                    criterionName
                )
            )
        } else {
            for (serverplayerentity in targets) {
                if (action.applyToCriterion(serverplayerentity, advancementIn, criterionName)) {
                    ++i
                }
            }
            if (i == 0) {
                if (targets.size == 1) {
                    throw CommandException(
                        TranslationTextComponent(
                            action.prefix + ".criterion.to.one.failure",
                            criterionName,
                            advancementIn.displayText,
                            targets.iterator().next().displayName
                        )
                    )
                } else {
                    throw CommandException(
                        TranslationTextComponent(
                            action.prefix + ".criterion.to.many.failure",
                            criterionName,
                            advancementIn.displayText,
                            targets.size
                        )
                    )
                }
            } else {
                if (targets.size == 1) {
                    source.sendFeedback(
                        TranslationTextComponent(
                            action.prefix + ".criterion.to.one.success",
                            criterionName,
                            advancementIn.displayText,
                            targets.iterator().next().displayName
                        ), true
                    )
                } else {
                    source.sendFeedback(
                        TranslationTextComponent(
                            action.prefix + ".criterion.to.many.success",
                            criterionName,
                            advancementIn.displayText,
                            targets.size
                        ), true
                    )
                }
                i
            }
        }
    }

    /**
     * Gets all advancements that match the given mode.
     */
    private fun getMatchingAdvancements(
        advancementIn: Advancement,
        mode: Mode
    ): List<Advancement> {
        val list: MutableList<Advancement> = Lists.newArrayList()
        if (mode.includesParents) {
            var advancement = advancementIn.parent
            while (advancement != null) {
                list.add(advancement)
                advancement = advancement.parent
            }
        }
        list.add(advancementIn)
        if (mode.includesChildren) {
            addAllChildren(advancementIn, list)
        }
        return list
    }

    /**
     * Recursively adds all children of the given advancement to the given list. Does not add the advancement itself to
     * the list.
     */
    private fun addAllChildren(
        advancementIn: Advancement,
        list: MutableList<Advancement>
    ) {
        for (advancement in advancementIn.children) {
            list.add(advancement)
            addAllChildren(advancement, list)
        }
    }

    internal enum class Action(name: String) {
        GRANT("grant") {
            /**
             * Applies this action to the given advancement.
             *
             * @return True if the player was affected.
             */
            override fun applyToAdvancement(
                player: ServerPlayerEntity?,
                advancementIn: Advancement?
            ): Boolean {
                val advancementprogress =
                    player!!.advancements.getProgress(advancementIn!!)
                return if (advancementprogress.isDone) {
                    false
                } else {
                    for (s in advancementprogress.remaningCriteria) {
                        player.advancements.grantCriterion(advancementIn, s)
                    }
                    true
                }
            }

            /**
             * Applies this action to the given criterion.
             *
             * @return True if the player was affected.
             */
            override fun applyToCriterion(
                player: ServerPlayerEntity?,
                advancementIn: Advancement?,
                criterionName: String?
            ): Boolean {
                return player!!.advancements.grantCriterion(advancementIn!!, criterionName!!)
            }
        },
        REVOKE("revoke") {
            /**
             * Applies this action to the given advancement.
             *
             * @return True if the player was affected.
             */
            override fun applyToAdvancement(
                player: ServerPlayerEntity?,
                advancementIn: Advancement?
            ): Boolean {
                val advancementprogress =
                    player!!.advancements.getProgress(advancementIn!!)
                return if (!advancementprogress.hasProgress()) {
                    false
                } else {
                    for (s in advancementprogress.completedCriteria) {
                        player.advancements.revokeCriterion(advancementIn, s)
                    }
                    true
                }
            }

            /**
             * Applies this action to the given criterion.
             *
             * @return True if the player was affected.
             */
            override fun applyToCriterion(
                player: ServerPlayerEntity?,
                advancementIn: Advancement?,
                criterionName: String?
            ): Boolean {
                return player!!.advancements.revokeCriterion(advancementIn!!, criterionName!!)
            }
        };

        @Suppress("JoinDeclarationAndAssignment")
        val prefix: String

        /**
         * Applies this action to all of the given advancements.
         *
         * @return The number of players affected.
         */
        fun applyToAdvancements(
            player: ServerPlayerEntity?,
            advancements: Iterable<Advancement>
        ): Int {
            var i = 0
            for (advancement in advancements) {
                if (applyToAdvancement(player, advancement)) {
                    ++i
                }
            }
            return i
        }

        /**
         * Applies this action to the given advancement.
         *
         * @return True if the player was affected.
         */
        protected abstract fun applyToAdvancement(
            player: ServerPlayerEntity?,
            advancementIn: Advancement?
        ): Boolean

        /**
         * Applies this action to the given criterion.
         *
         * @return True if the player was affected.
         */
        abstract fun applyToCriterion(
            player: ServerPlayerEntity?,
            advancementIn: Advancement?,
            criterionName: String?
        ): Boolean

        init {
            prefix = "commands.advancement.$name"
        }
    }

    @Suppress("unused")
    internal enum class Mode(
        val includesParents: Boolean,
        val includesChildren: Boolean
    ) {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true)
    }
}
