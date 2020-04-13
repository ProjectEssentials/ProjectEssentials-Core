package com.mairwunnx.projectessentials.core.api.v1.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.ISuggestionProvider
import java.util.concurrent.CompletableFuture

class StringArrayArgument(private val array: List<String>) : ArgumentType<String> {
    override fun parse(reader: StringReader): String {
        val value = reader.readUnquotedString()
        return if (value in array) {
            array.find { it == value } ?: value
        } else {
            throw IllegalArgumentException("No settings found for name ${value}.")
        }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> = ISuggestionProvider.suggest(array.sorted(), builder)

    companion object {
        @JvmStatic
        fun with(array: List<String>) = StringArrayArgument(array)

        @JvmStatic
        fun getValue(
            context: CommandContext<CommandSource>, name: String
        ): String = context.getArgument(name, String::class.java)
    }
}
