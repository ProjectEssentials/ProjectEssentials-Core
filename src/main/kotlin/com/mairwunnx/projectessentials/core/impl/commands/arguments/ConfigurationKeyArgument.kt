package com.mairwunnx.projectessentials.core.impl.commands.arguments

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.ISuggestionProvider
import java.util.concurrent.CompletableFuture

class ConfigurationKeyArgument : ArgumentType<String> {
    val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general").take()
    }

    override fun parse(reader: StringReader): String {
        val value = reader.readUnquotedString()
        val keys = generalConfiguration.keys.asSequence().map { it.toString() }
        return if (value in keys) {
            keys.find { it == value } ?: value
        } else throw IllegalArgumentException("No such value found $value in configuration.")
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>, builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> = ISuggestionProvider.suggest(
        generalConfiguration.keys.asSequence().map { it.toString() }.asIterable(), builder
    )

    companion object {
        @JvmStatic
        fun getValue(
            context: CommandContext<CommandSource>, name: String
        ) = context.getArgument(name, String::class.java)!!
    }
}
