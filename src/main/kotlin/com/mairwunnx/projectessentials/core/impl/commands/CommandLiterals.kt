package com.mairwunnx.projectessentials.core.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.arguments.StringArrayArgument
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands

fun takeConfigureEssentialsLiteral(): LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("configure-essentials").then(
        Commands.argument("setting", StringArrayArgument.with(
            ConfigurationAPI.getConfigurationByName<GeneralConfiguration>(
                "general"
            ).take().keys.map { it.toString() }
        )).then(
            Commands.literal("set").then(
                Commands.argument("value", StringArgumentType.string()).executes(
                    ConfigureEssentialsCommand::process
                )
            )
        )
    )
