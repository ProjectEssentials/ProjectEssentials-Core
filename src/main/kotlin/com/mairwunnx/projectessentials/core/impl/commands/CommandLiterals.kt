package com.mairwunnx.projectessentials.core.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider

inline val configureEssentialsLiteral: LiteralArgumentBuilder<CommandSource>
    get() = Commands.literal("configure-essentials").then(
        Commands.argument(
            "setting", StringArgumentType.string()
        ).suggests { _, builder ->
            ISuggestionProvider.suggest(
                getConfigurationByName<GeneralConfiguration>(
                    "general"
                ).take().keys.map { it as String }, builder
            )
        }.then(
            Commands.literal("set").then(
                Commands.argument("value", StringArgumentType.string()).executes {
                    ConfigureEssentialsCommand.process(it)
                }
            )
        )
    )
