package com.mairwunnx.projectessentials.core.api.v1.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.ISuggestionProvider
import java.util.concurrent.CompletableFuture

/**
 * String argument type class, for commands.
 *
 * @param array array with accepted values for command.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
class StringArrayArgument(private val array: List<String>) : ArgumentType<String> {
    override fun parse(reader: StringReader): String {
        val value = reader.readUnquotedString()
        return if (value in array) {
            array.find { it == value } ?: value
        } else {
            throw IllegalArgumentException("No value found for name $value in array.")
        }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> = ISuggestionProvider.suggest(array.sorted(), builder)

    companion object {
        /**
         * Creates new instance of [StringArrayArgument].
         *
         * @param array array with accepted value for command.
         * @return new instance of [StringArrayArgument] for command.
         * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
         */
        @JvmStatic
        fun with(array: List<String>) = StringArrayArgument(array)

        /**
         * @param context command context with parameter type [CommandSource].
         * @param name argument name what contains your argument value.
         * @throws IllegalArgumentException when argument not exist in command context.
         * @return selected value by player in command.
         * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
         */
        @JvmStatic
        fun getValue(
            context: CommandContext<CommandSource>, name: String
        ): String = context.getArgument(name, String::class.java)
    }
}
