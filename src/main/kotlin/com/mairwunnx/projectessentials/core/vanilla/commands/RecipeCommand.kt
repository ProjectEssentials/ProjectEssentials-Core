/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ResourceLocationArgument
import net.minecraft.command.arguments.SuggestionProviders
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object RecipeCommand {
    private val GIVE_FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.recipe.give.failed")
    )
    private val TAKE_FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.recipe.take.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.recipe + "recipe"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["recipe"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/recipe` vanilla command")
        NativeCommandUtils.removeCommand("recipe")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("give").then(
                        Commands.argument(
                            "targets", EntityArgument.players()
                        ).then(
                            Commands.argument(
                                "recipe", ResourceLocationArgument.resourceLocation()
                            ).suggests(
                                SuggestionProviders.ALL_RECIPES
                            ).executes { p_198588_0_ ->
                                giveRecipes(
                                    p_198588_0_.source,
                                    EntityArgument.getPlayers(p_198588_0_, "targets"),
                                    setOf(
                                        ResourceLocationArgument.getRecipe(
                                            p_198588_0_, "recipe"
                                        )
                                    )
                                )
                            }
                        ).then(
                            Commands.literal("*").executes { p_198591_0_ ->
                                giveRecipes(
                                    p_198591_0_.source,
                                    EntityArgument.getPlayers(p_198591_0_, "targets"),
                                    p_198591_0_.source.server.recipeManager.recipes
                                )
                            }
                        )
                    )
                ).then(
                    Commands.literal("take").then(
                        Commands.argument(
                            "targets", EntityArgument.players()
                        ).then(
                            Commands.argument(
                                "recipe", ResourceLocationArgument.resourceLocation()
                            ).suggests(
                                SuggestionProviders.ALL_RECIPES
                            ).executes { p_198587_0_: CommandContext<CommandSource> ->
                                takeRecipes(
                                    p_198587_0_.source,
                                    EntityArgument.getPlayers(p_198587_0_, "targets"),
                                    setOf(
                                        ResourceLocationArgument.getRecipe(
                                            p_198587_0_, "recipe"
                                        )
                                    )
                                )
                            }
                        ).then(
                            Commands.literal("*").executes { p_198592_0_ ->
                                takeRecipes(
                                    p_198592_0_.source,
                                    EntityArgument.getPlayers(p_198592_0_, "targets"),
                                    p_198592_0_.source.server.recipeManager.recipes
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
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.recipe", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "recipe")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.recipe.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }


    @Throws(CommandSyntaxException::class)
    private fun giveRecipes(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        recipes: Collection<IRecipe<*>>
    ): Int {
        checkPermissions(source)

        var i = 0
        for (serverplayerentity in targets) {
            i += serverplayerentity.unlockRecipes(recipes)
        }
        return if (i == 0) {
            throw GIVE_FAILED_EXCEPTION.create()
        } else {
            if (targets.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.recipe.give.success.single",
                        recipes.size,
                        targets.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.recipe.give.success.multiple",
                        recipes.size,
                        targets.size
                    ), true
                )
            }
            i
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun takeRecipes(
        source: CommandSource,
        targets: Collection<ServerPlayerEntity>,
        recipes: Collection<IRecipe<*>>
    ): Int {
        var i = 0
        for (serverplayerentity in targets) {
            i += serverplayerentity.resetRecipes(recipes)
        }
        return if (i == 0) {
            throw TAKE_FAILED_EXCEPTION.create()
        } else {
            if (targets.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.recipe.take.success.single",
                        recipes.size,
                        targets.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.recipe.take.success.multiple",
                        recipes.size,
                        targets.size
                    ), true
                )
            }
            i
        }
    }
}
