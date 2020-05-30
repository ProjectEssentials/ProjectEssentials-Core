package com.mairwunnx.projectessentials.core.impl.commands

import com.mairwunnx.projectessentials.core.impl.commands.arguments.ConfigurationKeyArgument
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands

val configureEssentialsLiteral: LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("configure-essentials").then(
        Commands.argument("setting", ConfigurationKeyArgument()).then(
            Commands.literal("set").then(
                Commands.argument("value", StringArgumentType.string()).executes(
                    ConfigureEssentialsCommand::process
                )
            )
        )
    )
